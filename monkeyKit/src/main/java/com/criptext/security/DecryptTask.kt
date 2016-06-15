package com.criptext.security

import android.os.AsyncTask
import com.criptext.comunication.CBTypes
import com.criptext.comunication.MOKMessage
import com.criptext.comunication.MessageTypes
import com.criptext.socket.SecureSocketService
import java.lang.ref.WeakReference

/**
 * Created by gesuwall on 6/6/16.
 */

class DecryptTask(service: SecureSocketService): AsyncTask<EncryptedMsg, MOKMessage, Int>(){
    val serviceRef: WeakReference<SecureSocketService>
    init {
        serviceRef = WeakReference(service)
    }
    override fun doInBackground(vararg p0: EncryptedMsg?): Int? {
        for(encrypted in p0){
            if(decryptMessage(encrypted!!))
                publishProgress(encrypted.message)
        }

        return 0;
    }

    override fun onProgressUpdate(vararg values: MOKMessage?) {
        val service = serviceRef.get()
        service?.executeInDelegate(CBTypes.onMessageReceived, Array(0, { i -> values[i] as Any}));
    }

    companion object {
        fun decryptMessage(encrypted: EncryptedMsg): Boolean{
            try {
                val message = encrypted.message
                if (message.encr == "1" && !message.type.equals(MessageTypes.MOKFile)) {
                    message.msg = AESUtil.decryptWithCustomKeyAndIV(message.msg,
                            encrypted.key, encrypted.iv);
                    message.encr = "0"
                }

                return true
            } catch (ex: Exception){
                ex.printStackTrace()
                return false
            }
        }
    }

}