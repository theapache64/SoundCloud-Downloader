adb shell rm /storage/sdcard1/scd.db
adb shell "su -c 'cp /data/data/com.theah64.soundclouddownloader/databases/scd.db /storage/sdcard1/scd.db'"
adb pull /storage/sdcard1/scd.db