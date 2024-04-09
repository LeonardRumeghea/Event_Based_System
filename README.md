# Event Based System - Practical Homework

# Parallelization
* Threads were implemented in the application in order to parallelize operations. The amount of objects required by each thread to create subscriptions and publishes was shared evenly.
* Especially for subscriptions, the amount of items that must have specific fields was estimated in advance based on the percentages provided as a parameter and distributed to each thread.

# Testing Results
* The tests were performed on an Intel Core i7-13700H CPU with 14 cores and 20 logical processors, up to 5 GHz frequency, and 32 GB of DDR5 RAM at 5200 MHz.

|               |    Total Number       | Pubs Creation Time (ms) | Subs Creation Time (ms) |
|---------------|:------------------:|:-----------------------:|:-----------------------:|
| One Thread    |   100.000          |   18 ms                 |   312 ms                |
| 16 Threads    |   100.000          |   67 ms                 |   197 ms                |
| One Thread    |   2.500.000        |   338 ms                |   225.091 ms            |
| 1024 Threads  |   2.500.000        |   1.686 ms              |   3.087 ms              |
