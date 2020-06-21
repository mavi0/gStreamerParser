mkdir /log
mkdir /share
touch /share/qoe.json

while :
do
    echo "Starting gst"
    gst-launch-1.0 -vvv playbin uri='http://dash.akamaized.net/dash264/TestCasesHD/2b/qualcomm/2/MultiRes.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H:%M:%.S' > /dash/raw.log)
    echo "Starting parser"
    java -jar /dash/out/parser.jar
    echo "Done.. Sleeping  1 hour"
    sleep 1h
done || exit 1

