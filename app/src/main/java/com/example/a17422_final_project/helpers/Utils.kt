package com.example.a17422_final_project.helpers

import com.google.mlkit.vision.common.PointF3D

/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * Utility methods for operations on [PointF3D].
 */
object Utils {
    fun add(a: PointF3D, b: PointF3D): PointF3D {
        return PointF3D.from(a.x + b.x, a.y + b.y, a.z + b.z)
    }

    fun subtract(b: PointF3D, a: PointF3D): PointF3D {
        return PointF3D.from(a.x - b.x, a.y - b.y, a.z - b.z)
    }

    fun multiply(a: PointF3D, multiple: Float): PointF3D {
        return PointF3D.from(a.x * multiple, a.y * multiple, a.z * multiple)
    }

    fun multiply(a: PointF3D, multiple: PointF3D): PointF3D {
        return PointF3D.from(
            a.x * multiple.x, a.y * multiple.y, a.z * multiple.z
        )
    }

    fun average(a: PointF3D, b: PointF3D): PointF3D {
        return PointF3D.from(
            (a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f, (a.z + b.z) * 0.5f
        )
    }

    fun l2Norm2D(point: PointF3D): Float {
        return Math.hypot(point.x.toDouble(), point.y.toDouble()).toFloat()
    }

    fun maxAbs(point: PointF3D): Float {
        return Math.max(Math.abs(point.x), Math.max(Math.abs(point.y), Math.abs(point.z)))
    }

    fun sumAbs(point: PointF3D): Float {
        return Math.abs(point.x) + Math.abs(point.y) + Math.abs(point.z)
    }

    fun addAll(pointsList: MutableList<PointF3D>, p: PointF3D) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(add(iterator.next(), p))
        }
    }

    fun subtractAll(p: PointF3D, pointsList: MutableList<PointF3D>) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(subtract(p, iterator.next()))
        }
    }

    fun multiplyAll(pointsList: MutableList<PointF3D>, multiple: Float) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(multiply(iterator.next(), multiple))
        }
    }

    fun multiplyAll(pointsList: MutableList<PointF3D>, multiple: PointF3D) {
        val iterator = pointsList.listIterator()
        while (iterator.hasNext()) {
            iterator.set(multiply(iterator.next(), multiple))
        }
    }
}