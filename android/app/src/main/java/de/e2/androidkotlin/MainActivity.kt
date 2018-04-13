package de.e2.androidkotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import android.support.constraint.ConstraintSet.PARENT_ID
import android.widget.TextView
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger {
    val ui = MainActivityUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info("set content view")
        toast("Hello")
        ui.setContentView(this)

//        ui.hello.text="rene"
    }
}

class MainActivityUI : AnkoComponent<Context> {
    lateinit var hello: TextView

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        constraintLayout {
            hello = textView("Hallo World")

            applyConstraintSet {
                hello {
                    connect(TOP to TOP of PARENT_ID margin dip(100),
                            LEFT to LEFT of PARENT_ID,
                            RIGHT to RIGHT of PARENT_ID,
                            BOTTOM to BOTTOM of PARENT_ID)
                }

            }
        }
    }
}