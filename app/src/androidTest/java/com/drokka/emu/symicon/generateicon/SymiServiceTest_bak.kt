/*********************************************
package com.drokka.emu.symicon.generateicon

import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider

import com.drokka.emu.symicon.generateicon.data.QuiltType
import org.junit.* //jupiter.api.Assertions.*
import com.drokka.emu.symicon.generateicon.data.SymIcon
import com.drokka.emu.symicon.generateicon.data.SymImageDefinition

internal class SymiServiceTest {

    val symDef = SymImageDefinition.defaultSimyDef(QuiltType.SQUARE)
    val intent = Intent(ApplicationProvider.getApplicationContext(), SymiService::class.java)
    val intArray = intArrayOf(  symDef.iterations,symDef.width, symDef.height  )
    val dArray = doubleArrayOf(
        symDef.lambda,
        symDef.alpha,
        symDef.beta,
        symDef.gamma,
        symDef.omega,
        symDef.ma
    )
    val inputSym = SymiGenerateInputData(intArray,'S'.toByte(),dArray)
    val symiService:SymiService = SymiService()
    @Test
    fun startService() {
        intent.putExtra("SymiGenerateInputData", inputSym).also {
            symiService.startService(it)
        }
        var ss ="categories in intent "
        for(cc in intent.categories){ ss.plus(cc  + " ")}
        Log.i("test", ss)
        assert(ss.length >2)
    }

    @Test
    fun stopService() {
    }

    @Test
    fun bindService() {
    }
}
 ******************************/