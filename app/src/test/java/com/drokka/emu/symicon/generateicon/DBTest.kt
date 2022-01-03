package com.drokka.emu.symicon.generateicon

import android.content.pm.InstrumentationInfo
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.drokka.emu.symicon.generateicon.database.SymiDatabase
import org.junit.After
import org.junit.Before
import java.io.IOException
import kotlin.jvm.Throws
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.drokka.emu.symicon.generateicon.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


abstract class DBTest {
    protected lateinit var symiDatabase: SymiDatabase

    @Before
    fun initDB(){
       val context = InstrumentationRegistry.getInstrumentation().context   //ApplicationProvider.getApplicationContext<Context>()

        symiDatabase = Room.inMemoryDatabaseBuilder(context, SymiDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDB(){
        symiDatabase.close()
    }


    val label1 = "Test Default_Square"
    val iconDefID1 = UUID.randomUUID()
    var iconDef1 = com.drokka.emu.symicon.generateicon.data.IconDef(
        iconDefID1,
        0.6, 0.3, 0.2, 0.4, 0.2, 0.3,
        QuiltType.SQUARE
    )
    val symIconId1 = UUID.randomUUID()
    var symi1: SymIcon = com.drokka.emu.symicon.generateicon.data.SymIcon(symIconId1,
        iconDefID1,
        label1
    )

    val iconDefId3 = UUID.randomUUID()
    var iconDef3 =com.drokka.emu.symicon.generateicon.data.IconDef(
        iconDefId3,
        0.6, 0.3, 0.2, 0.4, 0.2, 0.3,
        QuiltType.FRACTAL
    )
    val symIconId3 = UUID.randomUUID()
    var symi3: SymIcon = com.drokka.emu.symicon.generateicon.data.SymIcon(
        symIconId3,
        iconDefId3,
        "A fractal"
    )
    var def1: GeneratorDef = com.drokka.emu.symicon.generateicon.data.GeneratorDef(
        UUID.randomUUID(),
        symIconId1,
        com.drokka.emu.symicon.generateicon.data.TINY,
        com.drokka.emu.symicon.generateicon.data.TINY,
        10000
    )

    var def3: GeneratorDef = com.drokka.emu.symicon.generateicon.data.GeneratorDef(
        UUID.randomUUID(),
        symIconId3,
        com.drokka.emu.symicon.generateicon.data.TINY,
        com.drokka.emu.symicon.generateicon.data.TINY,
        10000
    )
    val filename = "filename"
    val byteArray1 = ByteArray(5) { i -> 1 }
    val len1 = byteArray1.size
    val byteArray2 = ByteArray(5){i -> 2}



    var generatedIcon = GeneratedIcon(
        UUID.randomUUID(),def1.gen_def_id, "generated data string",
        "generated data file name")
    var generatedIcon2 = GeneratedIcon(
        UUID.randomUUID(),def1.gen_def_id, "generated data string two",
        "generated data file name two")

    var generatedIcon3 = GeneratedIcon(
        UUID.randomUUID(),def3.gen_def_id, "generated data string three",
        "generated data file name three")

    var gen1 = GeneratedImageData(UUID.randomUUID(),generatedIcon.id,filename, byteArray1, len1 )
    val gen2 = GeneratedImageData(UUID.randomUUID(),generatedIcon2.id,filename, byteArray2, len1 )
    val gen3 = GeneratedImageData(UUID.randomUUID(),generatedIcon3.id,filename, byteArray2, len1 )

    var symData1 = GeneratedIconAndImageData(generatedIcon,gen1)//GeneratedIconAndImageData(UUID.randomUUID(), generatedIcon,gen1 )
    var symData2 = GeneratedIconAndImageData( generatedIcon2,gen2 )
    var symData3 = GeneratedIconAndImageData( generatedIcon3,gen3 )

    @Test
    fun insertSymi(){
        symiDatabase.symiDao().addIconDef(iconDef1)

        symiDatabase.symiDao().addSymIcon(symi1)
        symiDatabase.symiDao().addGeneratorDef(def1)

        symiDatabase.symiDao().addGeneratedIcon(generatedIcon)

        var listIco : LiveData<List<GeneratedIcon>> = symiDatabase.symiDao().getGeneratedIconList()
        var listIcoVal = listIco.getWrappedValue()

        Assert.assertNotNull(listIcoVal)
        Assert.assertEquals(1, listIcoVal?.size)
    }

    @Test
    fun insertSymiAndData(){

        Assert.assertNotNull(symData1.generatedImageData)
        //INSERT symData1 **********
        symiDatabase.symiDao().addGeneratedIconAndImageData(iconDef1, symi1, def1, symData1)
        Log.i("test", "after added")


        val listS: LiveData<List<GeneratedIconAndImageData>> = symiDatabase.symiDao().getAllSymIconData()
        val listSvalue = listS.getWrappedValue()

        var listGenIm : LiveData<List<GeneratedImageData>> = symiDatabase.symiDao().getGeneratedImageDataList()
        var listGenImVal = listGenIm.getWrappedValue()

        var listIco : LiveData<List<GeneratedIcon>> = symiDatabase.symiDao().getGeneratedIconList()
        var listIcoVal = listIco.getWrappedValue()

        Log.i("test", "after listS assigned size is " +listSvalue?.size.toString())

        Assert.assertNotNull(listSvalue)
        Assert.assertNotNull(listGenImVal)
        Assert.assertNotNull(listIcoVal)

        Assert.assertEquals(1, listGenImVal?.size)
        Assert.assertEquals(1, listIcoVal?.size)

        Assert.assertEquals(
            "gen im icon def id",
            generatedIcon.id,
            listGenImVal?.get(0)?.gen_icon_id
        )


        Assert.assertEquals(1, listGenImVal?.size)

        Assert.assertEquals(1, listSvalue?.size)
        //  assertEquals("Symi label correct",label1, listSvalue?.get(0)?.generatedIcon?. definition?.symIcon?.label )
        Assert.assertNotNull(listGenImVal?.get(0)?.byteArray)
        Assert.assertEquals(
            "Symi data correct",
            byteArray1[0],
            listGenImVal?.get(0)?.byteArray?.get(0)
        )
        Assert.assertEquals("Symi data len correct", len1, listGenImVal?.get(0)?.len)

        //INSERT symData2
        symiDatabase.symiDao().addGeneratedIconAndImageData(iconDef1, symi1, def1, symData2)

        val listS1: LiveData<List<GeneratedIconAndImageData>> = symiDatabase.symiDao().getAllSymIconData()
        val listSvalue1 = listS1.getWrappedValue()

        Assert.assertEquals(2, listSvalue1?.size)
        //  assertEquals("Symi label correct",label1, listSvalue1?.get(1)?.generatedIcon?.definition?.symIcon?.label )
        Assert.assertEquals("Symi data correct",byteArray2[0], listSvalue1?.get(1)?.generatedImageData?.byteArray?.get(0) )

        var listSymIcon: LiveData<List<GeneratedIcon>> = symiDatabase.symiDao().getGeneratedIconList()
        var listSymiValu = listSymIcon.getWrappedValue()

        Assert.assertEquals("gen icon list two entries", 2, listSymiValu?.size)
        //   assertEquals("SymIcon data ok", symi1.definition.alpha, listSymiValu?.get(0)?.definition?.symIcon?.definition?.alpha)

        var listSymi: LiveData<List<SymIcon>> = symiDatabase.symiDao().getSymIconList()
        var listSymiVal= listSymi.getWrappedValue()

        Assert.assertNotNull("SymIcon table not empty", listSymiVal)
        Assert.assertEquals("one entry in SymIcon table", 1, listSymiVal?.size)

        //INSERT with different SymIcon
        symiDatabase.symiDao().addGeneratedIconAndImageData(iconDef3, symi3, def3, symData3)

        var listSymi3: LiveData<List<SymIcon>> = symiDatabase.symiDao().getSymIconList()

        val listSymiVal3 = listSymi3.getWrappedValue()
        Assert.assertEquals("two entry in SymIcon table", 2, listSymiVal3?.size)


    }

    // Add extension function to LiveData<T> USING androidx.lifecycle.Observer NOT java
    @Throws(InterruptedException::class)
    fun <T> LiveData<T>.getWrappedValue(): T? {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = androidx.lifecycle.Observer<T>{
            data= it
            latch.countDown()
        }
        val me = this
        // observeForever can only be called on the main thread

        GlobalScope.launch(context = Dispatchers.Main){me.observeForever(observer)}
        latch.await(2, TimeUnit.SECONDS)

        return data
    }
}