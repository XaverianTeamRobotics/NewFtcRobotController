package org.firstinspires.ftc.teamcode.scripts

import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.internals.base.HardwareManager.gamepad1
import org.firstinspires.ftc.teamcode.internals.base.HardwareManager.motors
import org.firstinspires.ftc.teamcode.internals.base.Script

/**
 * Script for continuously controlling a motor based on gamepad input.
 *
 * @property id The identifier for the motor.
 * @property inverted Boolean indicating if the motor direction is inverted.
 * @property input Lambda function to get the input value for the motor.
 */
class ContinuousMotorScript(
    private val id: Int = 0,
    private val inverted: Boolean = false,
    private val input: () -> Double = { (gamepad1.right_trigger - gamepad1.left_trigger).toDouble() }
) : Script() {
    private val motor = motors.get("cm$id", 0)

    /**
     * Initializes the script. This method is called once when the script is started.
     */
    override fun init() {}

    /**
     * Main loop for continuously controlling the motor. This method runs continuously.
     */
    override fun run() {
        var prev: Double = 0.0
        while (true) {
            val i = (if (inverted) -1.0 else 1.0) * input()
            if (i != prev) {
                motor.power = i
                RobotLog.v("CMS${id}: Set power to ${i}")
            }
            prev = i
        }
    }

    /**
     * Called when the script is stopped.
     */
    override fun onStop() {}

    companion object {
        /**
         * Creates a three-way input function based on positive, negative, and stop conditions.
         *
         * @param pos Lambda function to check the positive condition.
         * @param neg Lambda function to check the negative condition.
         * @param stop Lambda function to check the stop condition.
         * @return Lambda function that returns the input value based on the conditions.
         */
        fun threeWayInput(pos: () -> Boolean, neg: () -> Boolean, stop: () -> Boolean): () -> Double {
            var state = 0.0
            return {
                if (pos()) {
                    state = 1.0
                } else if (neg()) {
                    state = -1.0
                } else if (stop()) {
                    state = 0.0
                }
                state
            }
        }

        /**
         * Creates a two-button input function based on positive and negative conditions.
         *
         * @param pos Lambda function to check the positive condition.
         * @param neg Lambda function to check the negative condition.
         * @param pow The power value to set when the positive condition is met.
         * @return Lambda function that returns the input value based on the conditions.
         */
        fun twoButtonInput(pos: () -> Boolean, neg: () -> Boolean, pow: Double = 1.0): () -> Double {
            return {
                if (pos()) pow
                else if (neg()) -pow
                else 0.0
            }
        }
    }
}