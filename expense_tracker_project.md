# 📱✨ Mobile Application Development I – Project Summary

> **Course:** Mobile Application Development I  
> **Instructor:** Heng Sopheak  
> **Student:** Min Phanith  
> **Project:** Expense Tracker Android Application

---



This repository contains the complete implementation of the **Expense Tracker Android Application**, developed progressively from **Lab 1 to Lab 8**. Each lab builds on the previous one, adding new features and improving functionality, architecture, and user experience.

---

## 🧪 Lab 1: Project Setup & Basic UI
**Objective:** Initialize the Android project and design basic user interfaces.

### Key Work Done:
- Created Android project using **Java**
- Set up project structure and packages
- Designed basic layouts using **XML**
- Implemented initial screens (Home, Add Expense)

---

## 🧪 Lab 2: Navigation & Fragments
**Objective:** Implement navigation between screens using fragments.

### Key Work Done:
- Used **Fragments** for modular UI
- Implemented **BottomNavigationView**
- Managed fragment transactions using `FragmentManager`
- Preserved state on configuration changes

---

## 🧪 Lab 3: Data Models & Local Database
**Objective:** Store expense and category data locally.

### Key Work Done:
- Created **Expense** and **Category** model classes
- Implemented **Room Database** (DAO, Entity, DatabaseHelper)
- Inserted and retrieved data from local database
- Loaded default categories

---

## 🧪 Lab 4: Add Expense Feature
**Objective:** Allow users to add new expenses.

### Key Work Done:
- Implemented **AddExpenseFragment**
- Input validation for amount, date, currency, and category
- Date picker integration
- Save expense to database / backend

---

## 🧪 Lab 5: Expense List & Detail View
**Objective:** Display expenses and view details.

### Key Work Done:
- Implemented **ExpenseListFragment** with RecyclerView
- Created **ExpenseAdapter**
- Added **ExpenseDetailFragment**
- Handled navigation and back stack correctly

---

## 🧪 Lab 6: Media & Extra Features
**Objective:** Enhance expense entries with media and usability improvements.

### Key Work Done:
- Added **camera and gallery** support for receipt images
- Handled runtime permissions (Camera, Storage)
- Saved images locally and displayed previews
- Improved UI/UX interactions

---

## 🧪 Lab 7: Backend Integration & Authentication
**Objective:** Connect app with backend services.

### Key Work Done:
- Integrated **Retrofit** for API communication
- Implemented backend API calls for expenses
- Added **Firebase Authentication** (Anonymous/User)
- Synced expenses with remote database

---

## 🧪 Lab 8: Notifications (Local & Push)
**Objective:** Implement local and push notifications.

### Key Work Done:
#### 🔔 Local Notification (Budget Warning)
- Created **Notification Channel**
- Implemented **Budget Limit Warning**:
  - USD > 100
  - KHR > 400,000
- Dynamic notification message using expense remark
- Handled **Android 13+ POST_NOTIFICATIONS permission**

#### 🚀 Push Notification (FCM)
- Integrated **Firebase Cloud Messaging (FCM)**
- Implemented `MyFirebaseMessagingService`
- Retrieved and used **FCM device token**
- Sent test notifications from Firebase Console
- Verified notifications work when app is **killed**
- Separated logic for **local vs push notifications**

---

## 🧠 Key Concepts Covered
- Android Activities & Fragments
- RecyclerView & Adapters
- Room Database
- Retrofit API Integration
- Firebase Authentication & FCM
- Runtime Permissions (Android 13+)
- Local & Push Notifications
- Clean architecture with utility helpers

---

---



## ✅ Final Status
✔ Labs 1–8 completed successfully  
✔ Application is fully functional  
✔ Ready for submission 🎉

---

**Author:** Min Phanith  
**Instructor:** Heng Sopheak  
**Course:** Mobile Application Development I  
**Project:** Expense Tracker

