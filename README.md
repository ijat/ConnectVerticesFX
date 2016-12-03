# ConnectVerticesFX

A JavaFX application that can read vertices file (*.cv).

Created by Ijat (http://ijat.my)

License: GPLv3 (Read LICENSE file for more info)

### How to Run?

+ Clone this repo and open this folder in IntelliJ IDEA
+ Open connectVerticesFX.java
+ Run
+ Try open some cv files

### CV File Example

Save this code as `example.cv`
```
7
0 50 45 1 3 4 
1 150 25 0 2 3 5 
2 250 155 1 4 6 
3 150 155 0 1 4 
4 30 255 0 2 3 6 
5 270 55 1 6 
6 60 95 2 4 5
```
* The first line stated the total vertices
* Other line shows the vertices data. Format `Index X-Axis Y-Axis [n-Connected Vertices]`