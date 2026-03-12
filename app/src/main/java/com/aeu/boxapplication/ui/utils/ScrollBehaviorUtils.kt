package com.aeu.boxapplication.ui.utils

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Creates a fling behavior with smooth momentum scrolling.
 * Use this for verticalScroll and horizontalScroll to ensure smooth scrolling.
 */
@Composable
fun rememberSmoothFlingBehavior(): FlingBehavior {
    return ScrollableDefaults.flingBehavior()
}

/**
 * Creates a scroll state configured for smooth scrolling.
 */
@Composable
fun rememberSmoothScrollState(initial: Int = 0): ScrollState {
    return androidx.compose.foundation.rememberScrollState(initial)
}

/**
 * Creates a lazy list state configured for smooth scrolling.
 */
@Composable
fun rememberSmoothLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    return androidx.compose.foundation.lazy.rememberLazyListState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset
    )
}

/**
 * Creates a custom decay animation spec for fling behavior
 * with adjusted friction for smoother scrolling.
 */
@Composable
fun <T> rememberSmoothDecayAnimationSpec(
    frictionMultiplier: Float = 1f
): DecayAnimationSpec<T> {
    val density = LocalDensity.current
    return exponentialDecay(
        frictionMultiplier = frictionMultiplier,
        absVelocityThreshold = with(density) { 0.dp.toPx() }
    )
}
