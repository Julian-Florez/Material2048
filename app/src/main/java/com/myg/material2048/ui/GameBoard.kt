package com.myg.material2048.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myg.material2048.model.Tile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TileView(tile: Tile, size: Dp, cornerRadius: Dp, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = getTileColors(tile.value)
    
    val density = LocalDensity.current
    val fontSize = remember(size, density) {
        with(density) {
            val targetPx = (size / 3.5f).toPx()
            val maxPx = 48.sp.toPx()
            targetPx.coerceAtMost(maxPx).toSp()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tile.value.toString(),
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun GameBoard(
    board: List<List<Tile?>>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val context = LocalContext.current
        val neutralColorId = context.resources.getIdentifier("system_neutral2_700", "color", "android")
        val gridColor = if (neutralColorId != 0) {
            Color(context.resources.getColor(neutralColorId, context.theme))
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

        // Calculate the available size for the board (assumed square or constrained by aspect ratio)
        val boardSize = if (maxWidth < maxHeight) maxWidth else maxHeight
        
        // Calculate dynamic corner radius
        val cornerRadius = boardSize * 0.05f 
        val tileCornerRadius = cornerRadius * 0.75f

        // Calculate cell size based on boardSize, subtracting padding (16.dp total) and spacing (24.dp total)
        // Formula: (TotalSize - BoxPadding*2 - Spacing*(GridSize-1)) / GridSize
        val cellSize = (boardSize - 16.dp - 24.dp) / 4

        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(cornerRadius))
                .background(gridColor)
                .padding(8.dp)
        ) {
            // Rejilla de fondo estática
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(
                                        color = gridColor,
                                        shape = RoundedCornerShape(tileCornerRadius)
                                    )
                            )
                        }
                    }
                }
            }

            // Contenedor para las fichas animadas
            Box(modifier = Modifier.fillMaxSize()) {
                val tilePositions = remember(board) {
                    board.flatMapIndexed { r, row ->
                        row.mapIndexedNotNull { c, tile ->
                            tile?.let { it to (r to c) }
                        }
                    }.associate { it }
                }

                val tilesToRender = mutableListOf<Triple<Tile, Pair<Int, Int>, Boolean>>() 

                tilePositions.forEach { (tile, position) ->
                    if (tile.mergedFrom != null && !tile.isFromUndo) {
                        // This is a merged tile.
                        val sources = tile.mergedFrom
                        if (sources.size == 2) {
                            val t1 = sources[0]
                            val t2 = sources[1]
                            
                            // t1 moving to position
                            tilesToRender.add(Triple(t1, position, true))
                            // t2 moving to position
                            tilesToRender.add(Triple(t2, position, true))
                            // The new merged tile
                            tilesToRender.add(Triple(tile, position, false))
                        } else {
                            tilesToRender.add(Triple(tile, position, false))
                        }
                    } else {
                        tilesToRender.add(Triple(tile, position, false))
                    }
                }

                tilesToRender.forEach { (tile, position, isTransient) ->
                    val key = if (isTransient) "transient_${tile.id}" else "tile_${tile.id}"
                    
                    key(key) {
                        AnimatedTile(
                            tile = tile,
                            targetPosition = position,
                            cellSize = cellSize,
                            cornerRadius = tileCornerRadius,
                            isTransient = isTransient
                        )
                    }
                }
            }
        }
    }
}

/**
 * Un contenedor que anima la posición y aparición de una ficha.
 */
@Composable
private fun AnimatedTile(
    tile: Tile,
    targetPosition: Pair<Int, Int>,
    cellSize: Dp,
    cornerRadius: Dp,
    isTransient: Boolean = false
) {
    val (targetRow, targetCol) = targetPosition

    val isFromUndo = tile.isFromUndo

    val isNew = tile.previousPosition == null
    val (startRow, startCol) = if (isFromUndo) targetPosition else (tile.previousPosition ?: targetPosition)

    // Calculate distance to determine animation type
    val distance = abs(startRow - targetRow) + abs(startCol - targetCol)
    val isFar = distance > 1

    val density = LocalDensity.current
    val startX = with(density) { ((cellSize + 8.dp) * startCol).toPx() }
    val startY = with(density) { ((cellSize + 8.dp) * startRow).toPx() }
    val targetX = with(density) { ((cellSize + 8.dp) * targetCol).toPx() }
    val targetY = with(density) { ((cellSize + 8.dp) * targetRow).toPx() }

    val animatedX = remember { Animatable(startX) }
    val animatedY = remember { Animatable(startY) }
    
    val startScale = if ((isNew && !isTransient) || isFromUndo) 0f else 1f
    val animatedScale = remember { Animatable(startScale) }
    
    val animatedAlpha = remember { Animatable(1f) }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(targetPosition, cellSize, isFromUndo) {
        if (!isFromUndo) {
            launch {
                animatedX.animateTo(
                    targetX,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                animatedY.animateTo(
                    targetY,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        } else {
            animatedX.snapTo(targetX)
            animatedY.snapTo(targetY)
        }
    }

    LaunchedEffect(isTransient, isFar) {
        if (isTransient) {
            val delayTime = if (isFar) 150L else 50L
            delay(delayTime)
            animatedAlpha.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessHigh))
            isVisible = false
        } 
    }

    // Gestiona las animaciones de escala (aparición y fusión)
    LaunchedEffect(tile) {
        if (!isTransient) {
            if (tile.mergedFrom != null && !tile.isFromUndo) {
                // Lógica de "pop" reescrita
                val sources = tile.mergedFrom
                var maxSourceDist = 0
                if (sources.size == 2) {
                     val p1 = sources[0].previousPosition
                     val p2 = sources[1].previousPosition
                     if (p1 != null) maxSourceDist = maxOf(maxSourceDist, abs(p1.first - targetRow) + abs(p1.second - targetCol))
                     if (p2 != null) maxSourceDist = maxOf(maxSourceDist, abs(p2.first - targetRow) + abs(p2.second - targetCol))
                }
                
                val isMergeFar = maxSourceDist > 1
                val popDelay = if (isMergeFar) 150L else 50L

                // Aseguramos que empiece invisible hasta el momento del pop
                animatedScale.snapTo(0f)
                delay(popDelay)
                
                // Efecto pop más sutil
                animatedScale.snapTo(0.5f)
                animatedScale.animateTo(
                    1.1f, 
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
                animatedScale.animateTo(
                    1f, 
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            } else if (isNew || tile.isFromUndo) {
                // Animación de aparición
                if (animatedScale.value == 0f) {
                    delay(50)
                    animatedScale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            } else {
                // Caso normal: Ficha existente moviéndose o estática.
                // Aseguramos que tenga el tamaño correcto si no se está animando algo especial.
                if (animatedScale.value != 1f) {
                    animatedScale.snapTo(1f)
                }
            }
        }
    }

    if (isVisible) {
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedX.value.roundToInt(), animatedY.value.roundToInt()) }
                .size(cellSize)
                .graphicsLayer {
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                    alpha = animatedAlpha.value
                }
        ) {
            TileView(tile, cellSize, cornerRadius, Modifier.fillMaxSize())
        }
    }
}

@Composable
fun getTileColors(value: Int): Pair<Color, Color> {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    
    return remember(value, context, colorScheme) {
        val tone = when (value) {
            2 -> 50
            4 -> 100
            8 -> 200
            16 -> 300
            32 -> 400
            64 -> 500
            128 -> 600
            256 -> 700
            512 -> 800
            1024 -> 900
            else -> 1000
        }

        val colorId = context.resources.getIdentifier("system_accent1_$tone", "color", "android")
        
        val backgroundColor = if (colorId != 0) {
            Color(context.resources.getColor(colorId, context.theme))
        } else {
            colorScheme.primaryContainer
        }

        // Obtener colores "accent" profundos del sistema para el texto
        val textLightId = context.resources.getIdentifier("system_accent1_100", "color", "android")
        val textDarkId = context.resources.getIdentifier("system_accent1_800", "color", "android")

        val textLight = if (textLightId != 0) Color(context.resources.getColor(textLightId, context.theme)) else Color.White
        val textDark = if (textDarkId != 0) Color(context.resources.getColor(textDarkId, context.theme)) else Color.Black

        // Ajuste de contraste: forzar texto oscuro hasta tono 500 para mejor legibilidad
        val textColor = if (tone <= 400) textDark else textLight

        backgroundColor to textColor
    }
}
