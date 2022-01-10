package com.drokka.emu.symicon.generateicon

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.database.SymiDao
import com.drokka.emu.symicon.generateicon.database.SymiDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class SymDAOInstrumentedTest {

    protected lateinit var symiDatabase: SymiDatabase

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    @Test
    fun useAppContext() {
        // Context of the app under test.
       // val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.drokka.emu.symicon.generateicon", appContext.packageName)
    }

    private lateinit var dao:SymiDao

    @Before
    fun initDB(){
        // val context = ApplicationProvider.getApplicationContext<Context>()

        symiDatabase = Room.inMemoryDatabaseBuilder(appContext, SymiDatabase::class.java).build()
        dao = symiDatabase.symiDao()
    }


    @After
    @Throws(IOException::class)
    fun closeDB(){
        symiDatabase.close()
    }

    lateinit var label1:String
    lateinit var iconDefID1:UUID
    lateinit var iconDef1:IconDef
    lateinit var symi1: SymIcon
    lateinit var  symIconId1:UUID
lateinit var  iconDefId3:UUID
    lateinit var iconDef3:IconDef
    lateinit var symIconId3:UUID
    lateinit var symi3:SymIcon
    lateinit var def1: GeneratorDef
    lateinit var def3: GeneratorDef
    lateinit var filename:String
    lateinit var byteArray1:ByteArray

    var len1 = 0
    lateinit var byteArray2:ByteArray
    lateinit var generatedIcon:GeneratedIcon
    lateinit var generatedIcon2:GeneratedIcon
    lateinit var generatedIcon3:GeneratedIcon
    lateinit var gen1:GeneratedImageData
    lateinit var gen2:GeneratedImageData
    lateinit var gen3:GeneratedImageData
    lateinit var symData1:GeneratedIconAndImageData
    lateinit var symData2:GeneratedIconAndImageData
    lateinit var symData3:GeneratedIconAndImageData

    @Before
    fun setUpStuff() {
         label1 = "Test Default_Square"
         iconDefID1 = UUID.randomUUID()
        iconDef1 = com.drokka.emu.symicon.generateicon.data.IconDef(
            iconDefID1,
            0.6, 0.3, 0.2, 0.4, 0.2, 0.3,
            QuiltType.SQUARE
        )
        symIconId1 = UUID.randomUUID()
        symi1 = SymIcon(
            symIconId1,
            iconDefID1,
            label1
        )

        iconDefId3 = UUID.randomUUID()
        iconDef3 = com.drokka.emu.symicon.generateicon.data.IconDef(
            iconDefId3,
            0.6, 0.3, 0.2, 0.4, 0.2, 0.3,
            QuiltType.FRACTAL
        )
        symIconId3 = UUID.randomUUID()
        symi3 = SymIcon(
            symIconId3,
            iconDefId3,
            "A fractal"
        )
        def1 = com.drokka.emu.symicon.generateicon.data.GeneratorDef(
            UUID.randomUUID(),
            symIconId1,
            com.drokka.emu.symicon.generateicon.data.TINY,
            com.drokka.emu.symicon.generateicon.data.TINY,
            10000
        )

        def3 = com.drokka.emu.symicon.generateicon.data.GeneratorDef(
            UUID.randomUUID(),
            symIconId3,
            com.drokka.emu.symicon.generateicon.data.TINY,
            com.drokka.emu.symicon.generateicon.data.TINY,
            10000
        )
        filename = "filename"
        byteArray1 = ByteArray(5000011) { i -> 1 }
         len1 = byteArray1.size
         byteArray2 = ByteArray(5) { i -> 2 }


        generatedIcon = GeneratedIcon(
            UUID.randomUUID(), def1.gen_def_id, String(byteArray1),
            "generated data file name"
        )
        generatedIcon2 = GeneratedIcon(
            UUID.randomUUID(), def1.gen_def_id, "generated data string two",
            "generated data file name two"
        )

        generatedIcon3 = GeneratedIcon(
            UUID.randomUUID(), def3.gen_def_id, "generated data string three",
            "generated data file name three"
        )

         gen1 =
            GeneratedImageData(UUID.randomUUID(), generatedIcon.id, filename, byteArray1, len1)
         gen2 =
            GeneratedImageData(UUID.randomUUID(), generatedIcon2.id, filename, byteArray2, len1)
         gen3 =
            GeneratedImageData(UUID.randomUUID(), generatedIcon3.id, filename, byteArray2, len1)

         symData1 = GeneratedIconAndImageData(
            generatedIcon,
            gen1
        )//GeneratedIconAndImageData(UUID.randomUUID(), generatedIcon,gen1 )
         symData2 = GeneratedIconAndImageData(generatedIcon2, gen2)
         symData3 = GeneratedIconAndImageData(generatedIcon3, gen3)
    }
    @Test
    fun insertSymi(){
        dao.addIconDef(iconDef1)

        dao.addSymIcon(symi1)
        dao.addGeneratorDef(def1)

        dao.addGeneratedIcon(generatedIcon)

        var listIco : LiveData<List<GeneratedIcon>> = dao.getGeneratedIconList()
        var listIcoVal = listIco.getWrappedValue()

        assertNotNull(listIcoVal)
        assertEquals(1,listIcoVal?.size )
        val getDef1 = dao.getIconDefId(iconDef1)
        val getSymy1 = dao.getSymIconId(iconDef1.icon_def_id, symi1.label)
        val getGenDef1 = dao.getGenDefId(symi1.sym_icon_id, def1.width,def1.height,def1.iterations)

        assertEquals("icon def id retrieved", iconDef1.icon_def_id, getDef1)
        assertEquals("symi def id retrieved", symi1.sym_icon_id, getSymy1)
        assertEquals("gen def id retrieved", def1.gen_def_id, getGenDef1)

    }

    @Test
    fun insertSymiAndData(){
        dao.addIconDef(iconDef1)

        dao.addSymIcon(symi1)
        dao.addGeneratorDef(def1)

        dao.addGeneratedIcon(generatedIcon)
        assertNotNull(symData1.generatedImageData)
        //INSERT symData1 **********
        dao.addGeneratedIconAndImageData(iconDef1, symi1, def1, symData1)

        Log.i("test", "after added")


        val listS: List<GeneratedIconAndImageData>? = dao.getAllSymIconData()
        val listSvalue = listS //.getWrappedValue()

        var listGenIm : LiveData<List<GeneratedImageData>> = dao.getGeneratedImageDataList()
        var listGenImVal = listGenIm.getWrappedValue()

        var listIco : LiveData<List<GeneratedIcon>> = dao.getGeneratedIconList()
        var listIcoVal = listIco.getWrappedValue()

        Log.i("test", "after listS assigned size is " +listSvalue?.size.toString())

        assertNotNull(listSvalue)
        assertNotNull(listGenImVal)
        assertNotNull(listIcoVal)

        assertEquals(1, listGenImVal?.size )
        assertEquals(1, listIcoVal?.size )

        assertEquals("gen im icon def id",generatedIcon.id, listGenImVal?.get(0)?.gen_icon_id )
        assertEquals(1, listGenImVal?.size )

        assertEquals(1, listSvalue?.size )
      //  assertEquals("Symi label correct",label1, listSvalue?.get(0)?.generatedIcon?. definition?.symIcon?.label )
        assertNotNull(listGenImVal?.get(0)?.byteArray)
        assertEquals("Symi data correct",byteArray1[0], listGenImVal?.get(0)?.byteArray?.get(0) )
        assertEquals("Symi data len correct",len1, listGenImVal?.get(0)?.len )

        //INSERT symData2
        dao.addGeneratedIconAndImageData(iconDef1, symi1, def1, symData2)

        val listS1: List<GeneratedIconAndImageData>? = dao.getAllSymIconData()
        val listSvalue1 = listS1 //.getWrappedValue()

//        assertEquals(2, listSvalue1?.size )
      //  assertEquals("Symi label correct",label1, listSvalue1?.get(1)?.generatedIcon?.definition?.symIcon?.label )
//     Assert.assertEquals("Symi data correct",byteArray2[0], listSvalue1?.get(0)?.generatedImageData?.byteArray?.get(0) )

        var listSymIcon: LiveData<List<GeneratedIcon>> = dao.getGeneratedIconList()
        var listSymiValu = listSymIcon.getWrappedValue()

 //       assertEquals("gen icon list two entries", 2, listSymiValu?.size)
     //   assertEquals("SymIcon data ok", symi1.definition.alpha, listSymiValu?.get(0)?.definition?.symIcon?.definition?.alpha)

        var listSymi: LiveData<List<SymIcon>> = dao.getSymIconList()
        var listSymiVal= listSymi.getWrappedValue()

        assertNotNull("SymIcon table not empty",listSymiVal)
        assertEquals("one entry in SymIcon table", 1, listSymiVal?.size)

        //INSERT with different SymIcon
        assertNotNull(iconDef3)
        assertNotNull(symi3)
        assertNotNull(def3)
        assertNotNull(symData3)

        dao.addGeneratedIconAndImageData(iconDef3, symi3, def3, symData3)

        var listSymi3: LiveData<List<SymIcon>> = dao.getSymIconList()

        val listSymiVal3 = listSymi3.getWrappedValue()
        assertEquals("two entry in SymIcon table", 2, listSymiVal3?.size)
    }

    @Test
    fun byteArrays(){
        dao.addIconDef(iconDef1)

        dao.addSymIcon(symi1)
        dao.addGeneratorDef(def1)

        dao.addGeneratedIcon(generatedIcon)
        assertNotNull(symData1.generatedImageData)
        //INSERT symData1 **********
        dao.addGeneratedIconAndImageData(iconDef1, symi1, def1, symData1)
        val listS1: List<GeneratedIconAndImageData> = dao.getAllSymIconDataMinusByteArray()
        val listSvalue1 = listS1 //.getWrappedValue()
        assertNotNull(listSvalue1)
        assert(!listSvalue1!!.isEmpty())
        Log.i("byteArrays","listS1 size is " + listSvalue1.size)

        val fullList = dao.getAllSymIconData()
        assertNotNull(fullList)
        assertEquals(byteArray1.size, fullList?.get(0)?.generatedImageData?.byteArray?.size )
        assertEquals(byteArray1.size, fullList?.get(0)?.generatedIcon?.generatedData?.length )
        val list2 = dao.getSymIconData(iconDefID1)
        assert(list2.isNotEmpty())

        assertEquals(byteArray1.size, list2?.get(0)?.generatedImageData?.byteArray?.size )
        assertEquals(byteArray1.size, list2?.get(0)?.generatedIcon?.generatedData?.length )

        val symi = dao.getSymIconSizedData(iconDefID1, def1.height)
        assertNotNull(symi)
        assertEquals(byteArray1.size, symi.generatedImageData?.byteArray?.size )
        assertEquals(byteArray1.size, symi.generatedIcon?.generatedData?.length )

    }


    // Add extension function to LiveData<T> USING androidx.lifecycle.Observer NOT java
    @Throws(InterruptedException::class)
    fun <T> LiveData<T>.getWrappedValue(): T? {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = Observer<T>{
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