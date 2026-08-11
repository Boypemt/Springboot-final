สามาชิก :
1. เมธาสิทธิ์ พิบูลย์ศิลป์ 682110189
2. ปัณณวิชญ์ สิทธิตัน 682110181
3. สิรวิชญ์ ยวงคำ 682110199
4. สายกลาง จะวะนะ 682110198


# Data Dictionary: Plants vs Zombie Shop System

หมายเหตุ: Data Type ด้านล่างเป็นการกำหนดที่แนะนำ (ไดอะแกรมเดิมไม่ได้ระบุชนิดข้อมูลไว้) สามารถปรับได้ตามระบบจริง

---

## 1. Customers

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| id | INT | PK, Auto Increment | NOT NULL | รหัสลูกค้า |
| username | VARCHAR(50) | UNIQUE | NOT NULL | ชื่อผู้ใช้สำหรับเข้าสู่ระบบ |
| password | VARCHAR(255) | - | NOT NULL | รหัสผ่าน (จัดเก็บแบบ hashed) |
| number | VARCHAR(15) | - | NOT NULL | เบอร์โทรศัพท์ |
| email | VARCHAR(100) | UNIQUE | NOT NULL | อีเมลลูกค้า |
| Country | VARCHAR(50) | - | NULL | ประเทศ |
| City | VARCHAR(50) | - | NULL | จังหวัด/เมือง |
| District | VARCHAR(50) | - | NULL | อำเภอ/เขต |
| Sub-district | VARCHAR(50) | - | NULL | ตำบล/แขวง |
| Zipcode | VARCHAR(10) | - | NULL | รหัสไปรษณีย์ |

---

## 2. Orders

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| id | INT | PK, Auto Increment | NOT NULL | รหัสคำสั่งซื้อ |
| user_id | INT | FK → Customers.id | NOT NULL | ลูกค้าที่สั่งซื้อ |
| orderDate | DATETIME | - | NOT NULL | วันที่/เวลาที่สั่งซื้อ |
| status | VARCHAR(20) | - | NOT NULL | สถานะคำสั่งซื้อ (เช่น pending, paid, shipped, cancelled) |

---

## 3. OrderItems (Weak Entity)

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| Id | INT | PK (Partial Key ร่วมกับ Order_id) | NOT NULL | รหัสรายการสินค้าในคำสั่งซื้อ |
| Order_id | INT | FK → Orders.id | NOT NULL | คำสั่งซื้อที่รายการนี้สังกัดอยู่ (Identifying Owner) |
| Product_id | INT | FK → Products.id | NOT NULL | สินค้าที่ถูกสั่งซื้อ |
| Qty | INT | - | NOT NULL | จำนวนที่สั่งซื้อ |

หมายเหตุ: เป็น Weak Entity เพราะไม่มีตัวตนที่สมบูรณ์ถ้าไม่มี Orders — ถ้าลบ Order รายการ OrderItems ที่สังกัดจะถูกลบตามไปด้วย (ON DELETE CASCADE)

---

## 4. Products

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| id | INT | PK, Auto Increment | NOT NULL | รหัสสินค้า |
| plant_id | INT | FK → Plants.Id | NOT NULL | พืชที่สินค้านี้อ้างอิงถึง |
| price | DECIMAL(10,2) | - | NOT NULL | ราคาขาย |
| stock | INT | DEFAULT 0 | NOT NULL | จำนวนคงเหลือในสต็อก |

---

## 5. Plants

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| Id | INT | PK, Auto Increment | NOT NULL | รหัสพืช |
| Name | VARCHAR(100) | - | NOT NULL | ชื่อพืช |
| PlantType_id | INT | FK → Types.Id | NOT NULL | ประเภทของพืช |
| Description | TEXT | - | NULL | คำอธิบายพืช |
| Hp | INT | - | NOT NULL | ค่าพลังชีวิต |
| Dmg | INT | - | NOT NULL | ค่าความเสียหายที่สร้างได้ |
| SunCost | INT | - | NOT NULL | ต้นทุนซันในการใช้พืชนี้ (ตามธีมเกม) |
| ActionSpeed | FLOAT | - | NOT NULL | ความเร็วในการโจมตี/ทำงาน |

---

## 6. Types

| Field | Data Type | Constraint | Null | คำอธิบาย |
|---|---|---|---|---|
| Id | INT | PK, Auto Increment | NOT NULL | รหัสประเภทพืช |
| Type | VARCHAR(50) | - | NOT NULL | ชื่อประเภท (เช่น Attack, Defense, Sun Producer) |
| Description | TEXT | - | NULL | คำอธิบายประเภท |

---

## ความสัมพันธ์ทั้งหมด (Relationships)

| ตาราง A | ความสัมพันธ์ | ตาราง B | ประเภท |
|---|---|---|---|
| Customers | 1 : many | Orders | Identifying: No |
| Orders | 1 : many | OrderItems | Identifying: Yes (Weak Entity) |
| Products | 1 : many | OrderItems | Identifying: No |
| Plants | 1 : many | Products | Identifying: No |
| Types | 1 : many | Plants | Identifying: No |
