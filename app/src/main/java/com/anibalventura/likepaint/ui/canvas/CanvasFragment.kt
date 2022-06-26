package com.anibalventura.likepaint.ui.canvas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.color.colorChooser
import com.anibalventura.likepaint.R
import com.anibalventura.likepaint.databinding.FragmentCanvasBinding
import com.anibalventura.likepaint.utils.Constants
import com.anibalventura.likepaint.utils.snackBarMsg


class CanvasFragment : Fragment() {

    private var _binding: FragmentCanvasBinding? = null
    private val binding get() = _binding!!

    private var brushColor: Int = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCanvasBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.canvas = this
        binding.drawing = binding.drawingView

        eraser()
        brush()

        setHasOptionsMenu(true)
        onBackPressed()

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode == Activity.RESULT_OK && requestCode == Constants.GALLERY -> {
                try {
                    when {
                        data!!.data != null -> binding.ivBackground.setImageURI(data.data)
                        else -> snackBarMsg(requireView(), getString(R.string.error_parsing))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_canvas, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_about -> findNavController().navigate(R.id.action_canvasFragment_to_aboutFragment)
            R.id.option_settings -> findNavController().navigate(R.id.settingsActivity)
        }


        return super.onOptionsItemSelected(item)
    }




    fun brush() {
        binding.drawingView.setBrushColor(brushColor)

        binding.ibBrushSize.setOnLongClickListener {
            showBrushSizeDialog(false)
            binding.drawingView.setBrushColor(brushColor)
            return@setOnLongClickListener true
        }
    }

    fun eraser() {
        binding.drawingView.setBrushColor(Color.WHITE)

        binding.ibEraseDraw.setOnLongClickListener {
            showBrushSizeDialog(true)
            binding.drawingView.setBrushColor(Color.WHITE)
            return@setOnLongClickListener true
        }
    }

    @SuppressLint("CheckResult")
    fun brushColor() {
        @Suppress("DEPRECATION")
        val colors = intArrayOf(
            Color.BLACK, Color.RED, Color.BLUE, Color.GREEN,
            Color.YELLOW, Color.MAGENTA, Color.GRAY, Color.CYAN,
            resources.getColor(R.color.beige), resources.getColor(R.color.orange),
            resources.getColor(R.color.greenLight), resources.getColor(R.color.purpleBlue)
        )

        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.dialog_choose_color)
            colorChooser(colors, allowCustomArgb = true, showAlphaSelector = true) { _, color ->
                brushColor = color
                binding.drawingView.setBrushColor(brushColor)
            }
            positiveButton(R.string.dialog_select)
            negativeButton(R.string.dialog_negative)
        }
    }

    @SuppressLint("CheckResult")







    private fun showBrushSizeDialog(eraser: Boolean) {
        val sizes = listOf(
            BasicGridItem(R.drawable.brush_small, getString(R.string.brush_small)),
            BasicGridItem(R.drawable.brush_medium, getString(R.string.brush_medium)),
            BasicGridItem(R.drawable.brush_large, getString(R.string.brush_large))
        )

        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            when (eraser) {
                true -> title(R.string.dialog_choose_eraser_size)
                else -> title(R.string.dialog_choose_brush_size)
            }

            gridItems(sizes) { _, index, _ ->
                when (index) {
                    0 -> binding.drawingView.setBrushSize(5F)
                    1 -> binding.drawingView.setBrushSize(10F)
                    2 -> binding.drawingView.setBrushSize(20F)
                }
            }
        }
    }






    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.dialog_exit)
                        message(R.string.dialog_exit_message)


                        negativeButton(R.string.dialog_exit_confirmation) {
                            if (isEnabled) {
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                        }
                    }
                }
            }
        )
    }
}