package com.example.bricks


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Handler

import android.view.Display
import android.view.MotionEvent
import android.view.View
import java.util.Random

class GameView(context: Context) : View(context) {

    private var ballX: Float = 0f
    private var ballY: Float = 0f
    private var velocity = Velocity(30, 40) // initial velocity eka increase kranna
    private val handler: Handler = Handler()
    private val UPDATE_MILLIS: Long = 30
    private lateinit var runnable: Runnable
    private val textPaint: Paint = Paint()
    private val healthPaint: Paint = Paint()
    private val brickPaint: Paint = Paint()
    private val TEXT_SIZE: Float = 120f
    private var paddleX: Float = 0f
    private var paddleY: Float = 0f
    private var oldX: Float = 0f
    private var oldPaddleX: Float = 0f
    private var points: Int = 0
    private var life: Int = 3
    private lateinit var ball: Bitmap
    private lateinit var paddle: Bitmap
    private var dWidth: Int = 0
    private var dHeight: Int = 0
    private var ballWidth: Int = 0
    private var ballHeight: Int = 0
    private lateinit var mpHit: MediaPlayer
    private lateinit var mpMiss: MediaPlayer
    private lateinit var mpBreak: MediaPlayer
    private var random: Random = Random()
    private var bricks = arrayOfNulls<Brick>(100)
    private var numBricks: Int = 0
    private var brokenBricks: Int = 0
    private var gameOver: Boolean = false

    init {
        ball = BitmapFactory.decodeResource(resources, R.drawable.ball)
        paddle = BitmapFactory.decodeResource(resources, R.drawable.paddle)
        fetchContext()
        runnable = Runnable { invalidate() }

        mpHit = MediaPlayer.create(fetchContext(), R.raw.soundone)
        mpMiss = MediaPlayer.create(fetchContext(), R.raw.soundfour)
        mpBreak = MediaPlayer.create(fetchContext(), R.raw.soundthree)

        textPaint.color = Color.RED
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT

        healthPaint.color = Color.GREEN

        brickPaint.color = Color.rgb(135, 206, 250) // Light blue color


        val display: Display = (fetchContext() as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        dWidth = size.x
        dHeight = size.y

        ballX = random.nextInt(dWidth - 50).toFloat()
        ballY = (dHeight / 3).toFloat()
        paddleY = (dHeight * 4) / 5.toFloat()
        paddleX = (dWidth / 2 - paddle.width / 2).toFloat()
        ballWidth = ball.width
        ballHeight = ball.height
        createBrick()
    }

    private fun createBrick() {
        val brickWidth: Int = dWidth / 10 //BRICK hdnne mthana
        val brickHeight: Int = dHeight / 20
        for (column in 0 until 10) {
            for (row in 0 until 5) {
                bricks[numBricks] = Brick(row, column, brickWidth, brickHeight)
                numBricks++
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        ballX += velocity.getX()
        ballY += velocity.getY()
        if ((ballX >= dWidth - ball.width) || ballX <= 0) {
            velocity.setX(velocity.getX() * -1)
        }

        if (ballY <= 0) {
            velocity.setY(velocity.getY() * -1)
        }

        if (ballY > paddleY + paddle.height) {
            ballX = (1 + random.nextInt(dWidth - ball.width - 1)).toFloat()
            ballY = (dHeight / 3).toFloat()
            mpMiss.start()
            velocity.setX(xVelocity())
            velocity.setY(40)
            life--
            if (life == 0) {
                gameOver = true
                launchGameOver()
            }
        }

        if (ballX + ballWidth >= paddleX && ballX <= paddleX + paddle.width
            && ballY + ballHeight >= paddleY && ballY <= paddleY + paddle.height
        ) {
            mpHit.start()
            velocity.setX(velocity.getX() + 1)
            velocity.setY((velocity.getY() + 1) * -1)
        }

        canvas.drawBitmap(ball, ballX, ballY, null)
        canvas.drawBitmap(paddle, paddleX, paddleY, null)

        for (i in 0 until numBricks) {
            if (bricks[i]?.getVisibility() == true) {
                bricks[i]?.let { brick ->
                    canvas.drawRect(
                        (brick.column * brick.width + 1).toFloat(),
                        (brick.row * brick.height + 1).toFloat(),
                        (brick.column * brick.width + brick.width - 1).toFloat(),
                        (brick.row * brick.height + brick.height - 1).toFloat(),
                        brickPaint
                    )
                }
            }
        }

        canvas.drawText("$points", 20f, TEXT_SIZE, textPaint)

        when (life) {
            2 -> healthPaint.color = Color.YELLOW
            1 -> healthPaint.color = Color.RED
        }

        canvas.drawRect(
            (dWidth - 200).toFloat(),
            30f,
            (dWidth - 200 + 60 * life).toFloat(),
            80f,
            healthPaint
        )

        for (i in 0 until numBricks) {
            if (bricks[i]?.getVisibility() == true) {
                bricks[i]?.let { brick ->
                    if (ballX + ballWidth >= brick.column * brick.width
                        && ballX <= brick.column * brick.width + brick.width
                        && ballY <= brick.row * brick.height + brick.height
                        && ballY >= brick.row * brick.height
                    ) {
                        mpBreak?.start()
                        velocity.setY((velocity.getY() + 1) * -1)
                        brick.setInvisible()
                        points += 10
                        brokenBricks++
                        if (brokenBricks % 50 == 0) { //brick 50 kaduwata psse mekan aluth 50 enwa
                            createBrick()
                        }
                    }
                }
            }
        }

        if (brokenBricks == numBricks) {
            gameOver = true
        }

        if (!gameOver) {
            handler.postDelayed(runnable, UPDATE_MILLIS)
        }
    }

    private fun launchGameOver() {
        handler.removeCallbacksAndMessages(null)
        val intent = Intent(fetchContext(), GameOver::class.java)
        intent.putExtra("points", points)
        fetchContext().startActivity(intent)
        (fetchContext() as Activity).finish()
    }

    private fun xVelocity(): Int {
        val values = intArrayOf(-45, -40, -35, 35, 40, 45)
        return values[random.nextInt(6)]
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX: Float = event.getX()
        val touchY: Float = event.getY()
        if (touchY >= paddleY) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldX = event.getX()
                    oldPaddleX = paddleX
                }
                MotionEvent.ACTION_MOVE -> {
                    val shift: Float = oldX - touchX
                    val newPaddleX: Float = oldPaddleX - shift
                    paddleX = when {
                        newPaddleX <= 0 -> 0f
                        newPaddleX >= dWidth - paddle.width -> (dWidth - paddle.width).toFloat()
                        else -> newPaddleX
                    }
                }
            }
        }
        return true
    }


    private fun fetchContext(): Context {
        return context
    }
}
