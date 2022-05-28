package com.drokka.emu.symicon.generateicon.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.drokka.emu.symicon.generateicon.R
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData
import com.drokka.emu.symicon.generateicon.data.GeneratedImage
import java.io.File
import java.io.FileInputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val VIEW_PAGER_BIGS = "viewPager Bigs"

/**
 * A simple [Fragment] subclass.
 * Use the [BigImageViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BigImageViewPagerFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            
        }
    }

    var imageList: List<GeneratedIconWithAllImageData>? = null
    private lateinit var viewPager: ViewPager2
    private val viewModel:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_big_image_view_pager, container, false)

        viewPager = view.findViewById(R.id.bigImageViewPager)

         imageList = viewModel.getSymBigsList()
        viewPager.adapter = this.activity?.let { imageList?.let { it1 ->
            ViewBigImagePagerAdapter(
                it
            )
        } }

        Log.d(VIEW_PAGER_BIGS, "view inflated. Adapter count is: " + viewPager.adapter?.itemCount)

        if(viewPager.adapter?.itemCount!! >0)  {
            viewPager.currentItem = 0
            Log.d(VIEW_PAGER_BIGS, "more than zero bigs!!")
        }

        viewPager.registerOnPageChangeCallback( object:
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(VIEW_PAGER_BIGS, "onPageSelected position = " + position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d(VIEW_PAGER_BIGS, "onPageScrollStateChanged state = " + state)

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.d(VIEW_PAGER_BIGS, "onPageScrolledg position = " + position)

            }
        }
        )



     //   view.addOnUnhandledKeyEventListener { v, event -> when (event.action) {
     //       Event.KEY
      //  } }
        return view
    }


    override fun getView(): View? {
        imageList = viewModel.getSymBigsList()
        return super.getView()
    }

    private inner class ViewBigImagePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
                if(imageList != null) {return imageList!!.size}
                else {return 0}
            }

        override fun createFragment(position: Int): Fragment {
            var bitmapImage: Bitmap? = null


            val bitmapFile = imageList?.get(position)?.iconImageFileName
            val len = imageList?.get(position)?.len ?: 0
            try {
                val imagesDirPath = File(context?.filesDir, "images")

                val imFile = bitmapFile?.let { File(imagesDirPath, it) }

                val inputStream = FileInputStream(imFile?.path)

                var byteArray = ByteArray(len)
                inputStream.read(byteArray, 0, len)
                bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, len)
            } catch (xx: Exception) {
                Log.e("ViewBigImagePagerAdapter", "Error could not load bitmap:" + xx.message)
            }

            return BigImageFragment.newInstance(bitmapImage )
        }



    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BigImageViewPagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            BigImageViewPagerFragment().apply {
                arguments = Bundle().apply {
                }
              //  imageList = imageListIn
            }
    }
}