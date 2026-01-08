package com.mdn.lag

import android.net.VpnService
import android.content.Intent
import java.io.FileInputStream
import java.io.FileOutputStream

class MdnLagVpnService : VpnService() {

    private var running = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        running = true
        Thread { runVpn() }.start()
        return START_STICKY
    }

    private fun runVpn() {
        val vpn = Builder()
            .setSession("mdn_lag")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .establish() ?: return

        val input = FileInputStream(vpn.fileDescriptor)
        val output = FileOutputStream(vpn.fileDescriptor)
        val buffer = ByteArray(32767)

        while (running) {
            val len = input.read(buffer)
            if (len > 0) {
                Thread.sleep(300)
                output.write(buffer, 0, len)
            }
        }
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}
