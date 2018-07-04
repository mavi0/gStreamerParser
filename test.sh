gst-launch-1.0 -vvv playbin uri='http://dash.akamaized.net/dash264/TestCasesHD/2b/qualcomm/2/MultiRes.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H:%M:%.S' > raw.log.3)
