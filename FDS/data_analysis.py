"""
============================================================
PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS SYSTEM
Subject: Foundations of Data Science (FDS - 25ITC31)
Python Data Retrieval, Preprocessing, Statistical Modeling & Visualization
============================================================
"""

import sys
import os
import sqlite3
import csv
import math
from datetime import datetime

# Try importing external analytical libraries; gracefully fall back if not yet installed
MATPLOTLIB_AVAILABLE = False
PANDAS_AVAILABLE = False

try:
    import pandas as pd
    PANDAS_AVAILABLE = True
except ImportError:
    pd = None

try:
    import matplotlib
    matplotlib.use('Agg') # Headless backend for CMD execution
    import matplotlib.pyplot as plt
    MATPLOTLIB_AVAILABLE = True
except ImportError:
    plt = None


def get_db_connection():
    candidates = [
        os.path.join(os.path.dirname(__file__), "..", "DBMS", "pharmacy.db"),
        os.path.join(os.path.dirname(__file__), "..", "pharmacy.db"),
        os.path.join(os.path.dirname(__file__), "pharmacy.db"),
        "DBMS/pharmacy.db",
        "pharmacy.db",
        "../DBMS/pharmacy.db"
    ]
    for path in candidates:
        if os.path.exists(path):
            return sqlite3.connect(path)
    # Default fallback
    return sqlite3.connect("DBMS/pharmacy.db")


def ensure_directories():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    dirs = [
        os.path.join(base_dir, "dataset"),
        os.path.join(base_dir, "visualizations"),
        os.path.join(base_dir, "output")
    ]
    for d in dirs:
        os.makedirs(d, exist_ok=True)
    return base_dir


def export_datasets_to_csv(conn, base_dir):
    dataset_dir = os.path.join(base_dir, "dataset")
    tables = ["medicines", "suppliers", "purchases", "purchase_items", "sales", "sale_items", "stock"]
    for table in tables:
        cur = conn.cursor()
        cur.execute(f"SELECT * FROM {table}")
        rows = cur.fetchall()
        col_names = [description[0] for description in cur.description]
        csv_path = os.path.join(dataset_dir, f"{table}.csv")
        with open(csv_path, "w", newline="", encoding="utf-8") as f:
            writer = csv.writer(f)
            writer.writerow(col_names)
            writer.writerows(rows)


def print_ascii_bar_chart(title, items, max_bar_width=40):
    """
    Renders an ASCII/Unicode bar chart directly in Windows CMD
    items is a list of (label, value)
    """
    print(f"\n" + "=" * 65)
    print(f" {title.upper()} ")
    print("=" * 65)
    if not items:
        print("  [INFO] No data points available for this visualization.")
        return

    max_val = max(v for _, v in items) if items else 1
    if max_val == 0:
        max_val = 1
    max_label_len = max(len(str(lbl)) for lbl, _ in items)
    max_label_len = min(max_label_len, 22)

    for label, val in items:
        truncated_label = str(label)[:max_label_len].ljust(max_label_len)
        bar_len = int((val / max_val) * max_bar_width)
        bar_str = "█" * bar_len
        print(f" {truncated_label} | {bar_str:<{max_bar_width}} {val:g}")
    print("-" * 65)


def generate_svg_bar_chart(title, items, output_path, xlabel="", ylabel=""):
    """Generates an SVG chart without external dependencies"""
    if not items:
        return
    width, height = 750, 420
    margin_left, margin_bottom, margin_top, margin_right = 160, 60, 50, 50
    chart_w = width - margin_left - margin_right
    chart_h = height - margin_top - margin_bottom

    max_val = max(v for _, v in items) if items else 1
    if max_val == 0:
        max_val = 1

    svg = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" style="background:#1e1e1e; font-family:Arial,sans-serif;">',
        f'<rect width="{width}" height="{height}" fill="#1e1e1e"/>',
        f'<text x="{width/2}" y="32" fill="#00d2ff" font-size="18" font-weight="bold" text-anchor="middle">{title}</text>'
    ]

    n = len(items)
    bar_height = min(28, (chart_h - (n * 8)) / n) if n > 0 else 20
    gap = 8

    colors = ["#4CAF50", "#2196F3", "#FF9800", "#E91E63", "#9C27B0", "#00BCD4", "#FFC107", "#009688"]

    for i, (label, val) in enumerate(items):
        y = margin_top + i * (bar_height + gap)
        bw = (val / max_val) * chart_w
        color = colors[i % len(colors)]
        svg.append(f'<text x="{margin_left - 10}" y="{y + bar_height/2 + 5}" fill="#ffffff" font-size="12" text-anchor="end">{label}</text>')
        svg.append(f'<rect x="{margin_left}" y="{y}" width="{bw}" height="{bar_height}" rx="4" fill="{color}" />')
        svg.append(f'<text x="{margin_left + bw + 8}" y="{y + bar_height/2 + 5}" fill="#00ffcc" font-size="12" font-weight="bold">{val:g}</text>')

    svg.append(f'<line x1="{margin_left}" y1="{margin_top}" x2="{margin_left}" y2="{margin_top + chart_h}" stroke="#555555" stroke-width="2"/>')
    svg.append(f'<line x1="{margin_left}" y1="{margin_top + chart_h}" x2="{margin_left + chart_w}" y2="{margin_top + chart_h}" stroke="#555555" stroke-width="2"/>')
    svg.append('</svg>')

    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(svg))


# 1. Medicine Sales Visualization
def analyze_medicine_sales(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT m.name, COALESCE(SUM(si.quantity_sold), 0) as total_sold, COALESCE(SUM(si.subtotal), 0.0) as total_revenue
        FROM medicines m
        LEFT JOIN sale_items si ON m.medicine_id = si.medicine_id
        GROUP BY m.medicine_id, m.name
        HAVING total_sold > 0
        ORDER BY total_sold DESC
    """)
    rows = cur.fetchall()

    items = [(r[0], r[1]) for r in rows]
    print_ascii_bar_chart("Medicine-wise Sales (Units Sold)", items)

    # Save SVG chart
    svg_path = os.path.join(base_dir, "visualizations", "medicine_sales_chart.svg")
    generate_svg_bar_chart("Medicine-wise Total Sales (Quantity Sold)", items, svg_path)

    # If matplotlib is available, generate PNG
    if MATPLOTLIB_AVAILABLE:
        names = [r[0] for r in rows]
        qtys = [r[1] for r in rows]
        plt.figure(figsize=(10, 6))
        plt.barh(names, qtys, color='#1f77b4')
        plt.xlabel('Quantity Sold (Units)')
        plt.title('Medicine Sales Performance')
        plt.gca().invert_yaxis()
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "medicine_sales_chart.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 2. Daily Revenue Visualization
def analyze_daily_revenue(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT sale_date, SUM(total_amount) as daily_revenue
        FROM sales
        GROUP BY sale_date
        ORDER BY sale_date ASC
    """)
    rows = cur.fetchall()

    items = [(r[0], r[1]) for r in rows]
    print_ascii_bar_chart("Daily Revenue Trend (Rs.)", items)

    svg_path = os.path.join(base_dir, "visualizations", "daily_revenue_trend.svg")
    generate_svg_bar_chart("Daily Revenue Trend (Rs.)", items, svg_path)

    if MATPLOTLIB_AVAILABLE:
        dates = [r[0] for r in rows]
        revs = [r[1] for r in rows]
        plt.figure(figsize=(9, 5))
        plt.plot(dates, revs, marker='o', color='#2ca02c', linewidth=2.5)
        plt.title('Daily Revenue Trend')
        plt.xlabel('Sale Date')
        plt.ylabel('Revenue (Rs.)')
        plt.grid(True, linestyle='--', alpha=0.6)
        plt.xticks(rotation=45)
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "daily_revenue_trend.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 3. Stock Analysis Visualization
def analyze_stock(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT m.name, s.current_stock
        FROM medicines m
        JOIN stock s ON m.medicine_id = s.medicine_id
        ORDER BY s.current_stock DESC
    """)
    rows = cur.fetchall()

    items = [(r[0], r[1]) for r in rows]
    print_ascii_bar_chart("Inventory Stock Quantities per Medicine", items)

    svg_path = os.path.join(base_dir, "visualizations", "stock_quantities.svg")
    generate_svg_bar_chart("Available Stock Quantities", items, svg_path)

    if MATPLOTLIB_AVAILABLE:
        names = [r[0] for r in rows]
        stocks = [r[1] for r in rows]
        plt.figure(figsize=(10, 6))
        plt.barh(names, stocks, color='#ff7f0e')
        plt.xlabel('Units in Stock')
        plt.title('Available Stock Quantities')
        plt.gca().invert_yaxis()
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "stock_quantities.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 4. Top Selling Medicine Analysis
def analyze_top_selling(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT m.name, SUM(si.quantity_sold) as total_qty, SUM(si.subtotal) as total_rev
        FROM sale_items si
        JOIN medicines m ON si.medicine_id = m.medicine_id
        GROUP BY m.medicine_id, m.name
        ORDER BY total_qty DESC
        LIMIT 5
    """)
    rows = cur.fetchall()

    items = [(r[0], r[1]) for r in rows]
    print_ascii_bar_chart("Top 5 Best Selling Medicines", items)

    svg_path = os.path.join(base_dir, "visualizations", "top_selling_medicines.svg")
    generate_svg_bar_chart("Top 5 Best Selling Medicines (Units Sold)", items, svg_path)

    if MATPLOTLIB_AVAILABLE:
        names = [r[0] for r in rows]
        qtys = [r[1] for r in rows]
        plt.figure(figsize=(8, 5))
        plt.bar(names, qtys, color='#9467bd')
        plt.title('Top 5 Best Selling Medicines')
        plt.ylabel('Quantity Sold')
        plt.xticks(rotation=20)
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "top_selling_medicines.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 5. Low Stock Analysis
def analyze_low_stock(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT m.name, s.current_stock, s.reorder_level
        FROM medicines m
        JOIN stock s ON m.medicine_id = s.medicine_id
        WHERE s.current_stock <= s.reorder_level
        ORDER BY s.current_stock ASC
    """)
    rows = cur.fetchall()

    items = [(r[0], r[1]) for r in rows]
    print_ascii_bar_chart("Critical Low Stock / Out of Stock Medicines", items)

    svg_path = os.path.join(base_dir, "visualizations", "low_stock_analysis.svg")
    generate_svg_bar_chart("Low Stock Critical Items", items, svg_path)

    if MATPLOTLIB_AVAILABLE:
        names = [r[0] for r in rows]
        stocks = [r[1] for r in rows]
        plt.figure(figsize=(8, 5))
        plt.barh(names, stocks, color='#d62728')
        plt.xlabel('Current Stock (Units)')
        plt.title('Low Stock & Depleted Medicines')
        plt.gca().invert_yaxis()
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "low_stock_analysis.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 6. Expiry Analysis
def analyze_expiry(conn, base_dir):
    cur = conn.cursor()
    cur.execute("""
        SELECT 
            SUM(CASE WHEN expiry_date < DATE('now') THEN 1 ELSE 0 END) AS expired,
            SUM(CASE WHEN expiry_date >= DATE('now') AND expiry_date <= DATE('now', '+30 days') THEN 1 ELSE 0 END) AS near_expiry,
            SUM(CASE WHEN expiry_date > DATE('now', '+30 days') THEN 1 ELSE 0 END) AS valid
        FROM medicines
    """)
    row = cur.fetchone()
    expired, near_expiry, valid = row[0] or 0, row[1] or 0, row[2] or 0

    items = [
        ("EXPIRED", expired),
        ("NEAR EXPIRY (<=30d)", near_expiry),
        ("VALID (>30d)", valid)
    ]
    print_ascii_bar_chart("Medicine Expiry Distribution", items)

    svg_path = os.path.join(base_dir, "visualizations", "expiry_distribution.svg")
    generate_svg_bar_chart("Medicine Expiry Status Distribution", items, svg_path)

    if MATPLOTLIB_AVAILABLE:
        labels = ['Expired', 'Near Expiry', 'Valid']
        counts = [expired, near_expiry, valid]
        colors = ['#ff4444', '#ffbb33', '#00C851']
        plt.figure(figsize=(7, 7))
        plt.pie(counts, labels=labels, autopct='%1.1f%%', colors=colors, startangle=140)
        plt.title('Medicine Inventory Expiry Classification')
        plt.tight_layout()
        plt.savefig(os.path.join(base_dir, "visualizations", "expiry_distribution.png"))
        plt.close()

    print(f"  [SUCCESS] Visualizations saved to: {os.path.join(base_dir, 'visualizations')}")


# 7. Complete Statistical Analysis
def run_complete_analysis(conn, base_dir):
    print("\n" + "=" * 65)
    print(" EXECUTING COMPLETE PHARMACY DATA SCIENCE & EDA PIPELINE ")
    print("=" * 65)

    export_datasets_to_csv(conn, base_dir)
    print("  [SUCCESS] Preprocessed SQL datasets exported to CSV in 'dataset/' directory.")

    analyze_medicine_sales(conn, base_dir)
    analyze_daily_revenue(conn, base_dir)
    analyze_stock(conn, base_dir)
    analyze_top_selling(conn, base_dir)
    analyze_low_stock(conn, base_dir)
    analyze_expiry(conn, base_dir)

    # Compute Statistical Metrics for summary text
    cur = conn.cursor()
    cur.execute("SELECT total_amount FROM sales")
    sales_vals = [r[0] for r in cur.fetchall()]

    n = len(sales_vals)
    mean_sale = sum(sales_vals) / n if n > 0 else 0
    sorted_sales = sorted(sales_vals)
    median_sale = sorted_sales[n // 2] if n > 0 else 0
    variance = sum((x - mean_sale) ** 2 for x in sales_vals) / n if n > 0 else 0
    std_dev = math.sqrt(variance)

    summary_file = os.path.join(base_dir, "output", "statistical_summary.txt")
    with open(summary_file, "w", encoding="utf-8") as f:
        f.write("=" * 60 + "\n")
        f.write("PHARMACY SALES & INVENTORY STATISTICAL SUMMARY\n")
        f.write(f"Generated Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write("=" * 60 + "\n\n")
        f.write(f"Total Sales Transactions Analysed : {n}\n")
        f.write(f"Mean Sale Transaction Value       : Rs. {mean_sale:,.2f}\n")
        f.write(f"Median Sale Transaction Value     : Rs. {median_sale:,.2f}\n")
        f.write(f"Standard Deviation of Sales       : Rs. {std_dev:,.2f}\n")
        f.write(f"Total Cumulative Revenue          : Rs. {sum(sales_vals):,.2f}\n")
        f.write(f"Pandas Acceleration Installed     : {'YES' if PANDAS_AVAILABLE else 'NO (Native Fallback Active)'}\n")
        f.write(f"Matplotlib Engine Installed       : {'YES' if MATPLOTLIB_AVAILABLE else 'NO (SVG/ASCII Active)'}\n")
        f.write("\n" + "=" * 60 + "\n")

    print(f"\n  [SUCCESS] Statistical analysis summary saved to: {summary_file}")
    print("=" * 65 + "\n")


def main():
    base_dir = ensure_directories()
    conn = get_db_connection()

    mode = "7"
    if len(sys.argv) > 1:
        mode = sys.argv[1].strip()

    if mode in ["1", "sales"]:
        analyze_medicine_sales(conn, base_dir)
    elif mode in ["2", "revenue"]:
        analyze_daily_revenue(conn, base_dir)
    elif mode in ["3", "stock"]:
        analyze_stock(conn, base_dir)
    elif mode in ["4", "top_selling"]:
        analyze_top_selling(conn, base_dir)
    elif mode in ["5", "low_stock"]:
        analyze_low_stock(conn, base_dir)
    elif mode in ["6", "expiry"]:
        analyze_expiry(conn, base_dir)
    elif mode in ["7", "all"]:
        run_complete_analysis(conn, base_dir)
    else:
        run_complete_analysis(conn, base_dir)

    conn.close()


if __name__ == "__main__":
    main()
