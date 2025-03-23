package com.example.plantingapp

import android.app.Activity
import java.util.ArrayList

object ActivityCollector {
    private val acts = ArrayList<Activity>()

    fun addAct(act: Activity){
        acts.add(act)
    }

    fun removeAct(act: Activity){
        acts.remove(act)
    }

    fun finishAll(){
        for(act in acts){
            if(!act.isFinishing){
                act.finish()
            }
        }
        acts.clear()
    }
}