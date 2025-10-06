package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import jakarta.inject.Singleton
import kotlin.random.Random

/**
 * Use case for generating tetrominoes using the "bag randomizer" algorithm.
 * This ensures fair distribution of pieces - all 7 pieces appear once before any repeat.
 * This is the standard algorithm used in modern Tetris implementations.
 */
@Singleton
class GenerateTetrominoUseCase {
    
    private val bag = mutableListOf<TetrominoType>()
    private val random = Random.Default
    
    /**
     * Generates the next tetromino using the bag randomizer algorithm.
     * When the bag is empty, it refills with all 7 piece types in random order.
     */
    operator fun invoke(): Tetromino {
        if (bag.isEmpty()) {
            refillBag()
        }
        
        val type = bag.removeFirst()
        return Tetromino.create(type, rotation = 0)
    }
    
    /**
     * Refills the bag with all 7 tetromino types in random order.
     */
    private fun refillBag() {
        bag.clear()
        bag.addAll(TetrominoType.entries)
        bag.shuffle(random)
    }
    
    /**
     * Resets the generator state. Useful for starting a new game.
     */
    fun reset() {
        bag.clear()
    }
}
