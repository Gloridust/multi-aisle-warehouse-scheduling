CREATE TABLE IF NOT EXISTS warehouse (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  total_rows INTEGER,
  total_cols INTEGER,
  total_sides INTEGER,
  pallet_volume REAL,
  horizontal_speed REAL,
  vertical_speed REAL,
  entry_row INTEGER,
  entry_col INTEGER,
  entry_side INTEGER
);

CREATE TABLE IF NOT EXISTS storage_location (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  warehouse_id INTEGER,
  row_num INTEGER,
  col_num INTEGER,
  side_num INTEGER,
  status INTEGER,
  current_sku_id INTEGER,
  current_qty INTEGER,
  used_volume REAL
);

CREATE TABLE IF NOT EXISTS sku (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  category TEXT,
  unit_volume REAL,
  unit_weight REAL
);

CREATE TABLE IF NOT EXISTS inbound_order (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_no TEXT,
  status INTEGER,
  strategy_type TEXT,
  create_time TEXT
);

CREATE TABLE IF NOT EXISTS inbound_order_item (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER,
  sku_id INTEGER,
  quantity INTEGER
);

CREATE TABLE IF NOT EXISTS outbound_order (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_no TEXT,
  status INTEGER,
  create_time TEXT
);

CREATE TABLE IF NOT EXISTS outbound_order_item (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER,
  sku_id INTEGER,
  quantity INTEGER
);

CREATE TABLE IF NOT EXISTS outbound_result (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER,
  location_id INTEGER,
  sku_id INTEGER,
  picked_qty INTEGER,
  picked_volume REAL,
  access_distance REAL
);

CREATE TABLE IF NOT EXISTS allocation_result (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER,
  strategy_type TEXT,
  location_id INTEGER,
  sku_id INTEGER,
  allocated_qty INTEGER,
  allocated_volume REAL,
  access_distance REAL
);

CREATE TABLE IF NOT EXISTS simulation_result (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER,
  strategy_type TEXT,
  total_distance REAL,
  avg_distance REAL,
  space_utilization REAL,
  used_locations INTEGER,
  compute_time INTEGER
);

CREATE TABLE IF NOT EXISTS admin_user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT UNIQUE,
  password_hash TEXT,
  updated_at TEXT
);

INSERT OR IGNORE INTO admin_user (id, username, password_hash, updated_at)
VALUES (1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', datetime('now'));

CREATE TABLE IF NOT EXISTS account_profile (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT UNIQUE,
  nickname TEXT,
  email TEXT,
  phone TEXT,
  avatar_base64 TEXT,
  updated_at TEXT
);

CREATE TABLE IF NOT EXISTS account_log (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT,
  action TEXT,
  detail TEXT,
  created_at TEXT
);

INSERT OR IGNORE INTO account_profile (id, username, nickname, email, phone, avatar_base64, updated_at)
VALUES (1, 'admin', '管理员', '', '', '', datetime('now'));
