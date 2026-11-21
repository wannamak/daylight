./gradlew build && ./daylight.sh
cat /tmp/ytics.txt
# and copy this into plot.txt
# adjust plot.txt for screen or pdf output
gnuplot -p plot.txt > /tmp/plot.pdf
