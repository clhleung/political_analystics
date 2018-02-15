# run.sh
# shell script to compile & run the Java programs that implement the required features


javac ./src/Record.java ./src/Analyze.java
java -cp ./src/ Analyze "itcont.txt" "percentile.txt"