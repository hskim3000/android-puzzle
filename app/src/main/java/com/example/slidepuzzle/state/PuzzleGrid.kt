package com.example.slidepuzzle.state

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Size
import com.example.slidepuzzle.utils.BitmapTile
import kotlin.*

typealias Puzzle2DArray = Array<Array<PuzzleDescriptor?>>

enum class Direction(val offsetX: Int, val offsetY: Int) {
    TOP(0, -1),
    BOTTOM(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0)
}

class PuzzleGrid(sourceImage: Bitmap, val size: Size, val missingSlides: Int = 1) {
    var bitmapTile = BitmapTile(sourceImage, size)
    var puzzles: Puzzle2DArray = genRandomSlides(false)

    private fun genRandomSlides(shuffle: Boolean = true): Puzzle2DArray  {
        val len = size.width * size.height
        val list = MutableList(len) { index ->
            if (index >= len - missingSlides)
                null
            else
                PuzzleDescriptor(
                    index,
                    bitmapTile.tiles[index / size.width][index % size.width]
                )
        }

        if (shuffle)
            list.shuffle()

        val array = list.toTypedArray()
        return Array(size.height) { y ->
            Array(size.width) { x ->
                array[(y * size.width) + (x % size.width)]
            }
        }
    }

    fun checkSlideMoveDirection(p: Point): Direction? {
        enumValues<Direction>().forEach { dir ->
            if (dir.offsetX + p.x < size.width
                    && dir.offsetY + p.y < size.height
                    && dir.offsetX + p.x >= 0
                    && dir.offsetY + p.y >= 0
                    && puzzles[p.y + dir.offsetY][p.x + dir.offsetX] == null) {
                return dir
            }
        }

        return null
    }

    fun shuffle(reset: Boolean = false) {
        puzzles = genRandomSlides(!reset)
    }

    fun moveSlide(p: Point): Point? {
        val direction = checkSlideMoveDirection(p)

        direction?.let {
            val newPoint = Point(
                p.x + direction.offsetX,
                p.y + direction.offsetY
            )
            puzzles[newPoint.y][newPoint.x] = puzzles[p.y][p.x]
            puzzles[p.y][p.x] = null

            return newPoint
        }

        return null
    }
}