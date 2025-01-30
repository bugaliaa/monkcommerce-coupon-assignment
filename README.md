# **MonkCommerce Coupon Management API**

## **Overview**
The **MonkCommerce Coupon Management API** provides functionalities for creating, retrieving, applying, and managing discount coupons for an e-commerce platform. It supports **three types of coupons**:

1. **Buy X Get Y Free Coupons (BOGO)**
2. **Product-Specific Discounts**
3. **Cart Value-Based Discounts**

The API ensures that **only the best possible coupon** is applied for maximum savings.

---

## **Features**
- Create, update, and delete coupons
- Fetch all active and applicable coupons
- Apply the **best** coupon for a cart
- Apply a **specific coupon** by coupon code
- Ensure **polymorphism** for different coupon types
- Validate and check **coupon applicability**
- Maintain **unique coupon codes**

---

## **Technologies Used**
- **Spring Boot** (Java)
- **Spring Data MongoDB**
- **Lombok** (for cleaner code)
- **Maven** (dependency management)

---

## **Coupon Types**
### **1. Buy X Get Y Free Coupon (BOGO)**
- Requires the customer to **buy a certain quantity** of a product to get another product **for free**.
- Example: **Buy 2 shirts, Get 1 free**

### **2. Product-Specific Discount**
- A discount is applied **only to a specific product**.
- Example: **10% off on Shoes**

### **3. Cart Value-Based Discount**
- The discount applies if the **total cart value** reaches a minimum amount.
- Example: **$20 off for orders above $150**

---

## **API Endpoints**

### **1. Create a Coupon**
- **Endpoint:** `POST /api/coupons`
- **Request:**
```json
{
  "type": "PRODUCT",
  "name": "10% Off Electronics",
  "productId": 100,
  "discount": 10.0,
  "active": true
}
```
- **Response:**
```json
{
  "data": {
    "couponCode": "ELEC10",
    "type": "PRODUCT",
    "name": "10% Off Electronics",
    "active": true
  },
  "message": "OK",
  "status": "OK"
}
```

---

### **2. Get All Coupons**
- **Endpoint:** `GET /api/coupons`
- **Response:**
```json
{
  "data": [
    {
      "couponCode": "ELEC10",
      "type": "PRODUCT",
      "name": "10% Off Electronics",
      "active": true
    }
  ],
  "message": "OK",
  "status": "OK"
}
```

---

### **3. Get a Coupon by ID**
- **Endpoint:** `GET /api/coupons/{id}`
- **Response:**
```json
{
  "data": {
    "couponCode": "ELEC10",
    "type": "PRODUCT",
    "name": "10% Off Electronics",
    "active": true
  },
  "message": "OK",
  "status": "OK"
}
```

---

### **4. Update a Coupon**
- **Endpoint:** `PUT /api/coupons/{id}`
- **Request:**
```json
{
  "couponCode": "ELEC15",
  "discount": 15.0
}
```
- **Response:**
```json
{
  "data": {
    "couponCode": "ELEC15",
    "discount": 15.0
  },
  "message": "OK",
  "status": "OK"
}
```

---

### **5. Delete a Coupon**
- **Endpoint:** `DELETE /api/coupons/{id}`
- **Response:**
```json
{
  "message": "Coupon deleted successfully",
  "status": "OK"
}
```

---

### **6. Get Applicable Coupons for a Cart**
- **Endpoint:** `POST /api/coupons/applicable-coupons`

---

### **7. Apply a Specific Coupon to the Cart**
- **Endpoint:** `POST /api/coupons/apply-coupon/{couponCode}`

---

### **8. Apply the Best Coupon Automatically**
- **Endpoint:** `POST /api/coupons/apply-best-coupon`

---

## **Business Logic**
### **Coupon Selection Strategy**
- **If multiple coupons are applicable**, the one with the **highest discount** is applied.
- **Buy X Get Y Free coupons** count the **free product’s price** as the discount.
- **A coupon is valid only if the cart meets its conditions**.

---

## **Future Enhancements**
- **Coupon Expiry Dates** - Auto-disable expired coupons.
- **User-Specific Coupons** - Apply based on user eligibility.
- **Stackable Discounts** - Allow multiple coupons to combine.

---

## **Project Structure**
```
/src/main/java/com/monkcommerce/coupon/app
├── domain/coupons        # Coupon entities (CartCoupon, ProductCoupon, BuyGetCoupon)
├── repository/coupon     # MongoDB repositories
├── service/coupon        # Business logic layer
├── web/api               # REST controllers (CouponEndpoint)
├── web/dto/request       # DTOs for requests (CreateCouponRequestDTO, CartRequestDTO)
├── web/dto/response      # DTOs for responses (CommonResponseDTO, CartResponseDTO)
```

