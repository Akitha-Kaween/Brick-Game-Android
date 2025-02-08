package com.example.bricks

class Brick(row: Int, column: Int, width: Int, height: Int) {

    private var isVisible: Boolean = true
    var row: Int = row
        private set
    var column: Int = column
        private set
    var width: Int = width
        private set
    var height: Int = height
        private set

    fun setInvisible() {
        isVisible = false
    }

    fun getVisibility(): Boolean {
        return isVisible
    }
}
