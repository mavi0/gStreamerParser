gst-launch-1.0 -vvv playbin uri='https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H:%M:%.S' > raw.log.3)
