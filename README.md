# 🎢 Theme Park Simulator

**Data Structures & Algorithms – Final Project**

## 📌 Project Overview

This project is a console-based Theme Park Simulator developed to demonstrate practical implementation of fundamental data structures in a real-world system.

The system manages visitors, ride queues, timed events, and undo operations using custom-built data structures (no built-in Java collection classes for core logic).

---

## 📁 Project Structure

```
src/
 ├── Main.java
 ├── Park.java
 └── structures/
      ├── AVL.java
      ├── Event.java
      ├── MinHeap.java
      ├── queue.java
      ├── Ride.java
      ├── Undo.java
      ├── UndoStack.java
      └── Visitor.java
```

---

## 🧩 Core Components

### 🔹 AVL Tree (`AVL.java`)

Stores all visitors indexed by ID.
Supports fast:

* Insert
* Search
* Delete

---

### 🔹 Custom Queue (Linked List) (`queue.java`)

Each ride has its own manually implemented queue.

Features:

* FIFO behavior
* VIP priority insertion (VIPs are placed after the last VIP in queue)
* Removal of visitors from the middle of the queue
* Repositioning when a visitor becomes VIP

---

### 🔹 Min-Heap (`MinHeap.java`)

Manages simulation events ordered by time.

Event types include:

* `START_SERVICE`
* `FINISH_SERVICE`

The heap ensures events with smaller timestamps are processed first.

---

### 🔹 Stack for Undo (`UndoStack.java`, `Undo.java`)

Implements undo functionality for user commands.

Supports undo for operations such as:

* ADD_VISITOR
* DELETE_VISITOR
* JOIN_QUEUE
* LEAVE_QUEUE

⚠️ `TICK` operations cannot be undone.

---

### 🔹 Ride Management (`Ride.java`)

Each ride contains:

* Name
* Capacity
* Duration
* Queue of visitors
* Currently serving visitors

Default rides created at program start:

* `Jet` (capacity: 2, duration: 5 minutes)
* `Wheel` (capacity: 3, duration: 10 minutes)

---

### 🔹 Visitor Model (`Visitor.java`)

Each visitor has:

* ID
* Name
* Type (NORMAL / VIP)
* Arrival Time
* Current Ride (if any)

---

## 🧾 Supported Commands

```
ADD_VISITOR <id> <name>
DELETE_VISITOR <id>
VISITOR_INFO <id>

MAKE_VIP <id>

ADD_RIDE <name> <capacity> <duration>

JOIN_QUEUE <id> <rideName>
LEAVE_QUEUE <id> <rideName>

TICK <minutes>

UNDO
STATUS
REPORT
```

---

## ▶️ How to Run

### Using IntelliJ

1. Open the project
2. Run `Main.java`
3. Enter commands in the console

### Using Terminal

From project root:

```bash
javac -d out src/*.java src/structures/*.java
java -cp out Main
```

---

## 🧠 Key Learning Outcomes

* Practical implementation of AVL Tree
* Manual Linked List implementation
* Event-driven simulation using Min-Heap
* Stack-based Undo mechanism
* Real-time system state management

---

## 🎓 Course

Data Structures & Algorithms
Final Project – Theme Park Simulation
