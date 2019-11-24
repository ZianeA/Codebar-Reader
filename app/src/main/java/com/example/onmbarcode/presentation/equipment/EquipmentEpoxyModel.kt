package com.example.onmbarcode.presentation.equipment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import java.util.*
import kotlin.math.hypot

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    @EpoxyAttribute
    lateinit var dropdownMenuItemClickListener: AdapterView.OnItemClickListener

    @ExperimentalStdlibApi
    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)

        holder.apply {
            equipmentType.text = equipment.type.capitalize(Locale.FRENCH)
            equipmentBarcode.text = equipment.barcode.toString()

            dropdownMenu.setAdapter(
                ArrayAdapter(
                    view.context,
                    R.layout.dropdown_menu_popup_item,
                    equipmentConditions
                )
            )
            dropdownMenu.inputType = InputType.TYPE_NULL
            dropdownMenu.filters = emptyArray()
            dropdownMenu.setText(equipmentConditions[equipment.condition.ordinal], false)
            dropdownMenu.onItemClickListener = dropdownMenuItemClickListener
            dropdownMenu.isEnabled = equipment.scanState == ScanState.ScannedAndSynced

            // Show progress bar
            if (equipment.scanState == ScanState.PendingScan) {
                val progressBarColor = ContextCompat.getColor(view.context, android.R.color.white)
                progressBar.apply {
                    indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.MULTIPLY)
                    visibility = View.VISIBLE
                }
            }

            // Set cardview background color
            if (equipmentToAnimateBarcode == equipment.barcode
                && equipment.scanState != ScanState.PendingScan
            ) {
                view.post {
                    animateEquipmentColor(this)
                    equipmentToAnimateBarcode = -1
                }

            } else {
                val equipmentColor =
                    if (equipment.scanState == ScanState.ScannedAndSynced) scannedColor else notScannedColor
                cardView.setCardBackgroundColor(equipmentColor)
            }
        }
    }

    override fun unbind(holder: EquipmentHolder) {
        super.unbind(holder)
        holder.revealView.visibility = View.INVISIBLE
        holder.progressBar.visibility = View.INVISIBLE
    }

    private fun animateEquipmentColor(holder: EquipmentHolder) {
        holder.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //get the center for the clipping circle relative to view.
                val cx = revealView.width / 2
                val cy = revealView.height / 2

                // get the final radius for the clipping circle
                val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat() * 2

                // create the animator for this view (the start radius is zero)
                val anim =
                    ViewAnimationUtils.createCircularReveal(revealView, 0, cy, 0f, finalRadius)

                // start the animation
                progressBar.visibility = View.INVISIBLE
                revealView.visibility = View.VISIBLE
                anim.addListener(onEnd = {
                    cardView.setCardBackgroundColor(scannedColor)
                    revealView.visibility = View.INVISIBLE
                })
                anim.duration = ANIMATION_DURATION
                anim.start()
            } else {
                val anim = ObjectAnimator.ofObject(
                    cardView,
                    "cardBackgroundColor",
                    ArgbEvaluator(),
                    notScannedColor,
                    scannedColor
                )
                anim.duration = ANIMATION_DURATION
                anim.start()
            }
        }
    }

    companion object {
        private const val ANIMATION_DURATION: Long = 1000
        var equipmentToAnimateBarcode: Int = -1
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View
    var scannedColor: Int = 0
    var notScannedColor: Int = 0
    lateinit var equipmentConditions: Array<String>

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
        scannedColor = ContextCompat.getColor(view.context, R.color.scanned_and_synced)
        notScannedColor = ContextCompat.getColor(view.context, R.color.not_scanned)
        equipmentConditions = view.resources.getStringArray(R.array.equipment_condition)
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val cardView by bind<CardView>(R.id.equipmentCardView)
    val revealView by bind<ImageView>(R.id.revealView)
    val progressBar by bind<ProgressBar>(R.id.progressBar)
    val dropdownMenu by bind<AutoCompleteTextView>(R.id.dropdownMenu)
}