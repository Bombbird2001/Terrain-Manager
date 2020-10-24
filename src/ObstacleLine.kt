import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.*

class ObstacleLine(private val frame: Frame, private val removable: Boolean) {
    private var labelX = 0
    private var labelY = 0
    private val altitudeJTextField = JTextField()
    private val displayJTextField = JTextField()
    private val coordinatesJTextField = JTextField()
    private val removeButton = JButton("- Remove obstacle")

    /** Adds all the user input boxes */
    fun addBoxes(offset: Int) {
        altitudeJTextField.bounds = Rectangle(frame.altitudeJLabel.x, frame.altitudeJLabel.y + 35 + offset * 40, frame.altitudeJLabel.width, 25)
        displayJTextField.bounds = Rectangle(frame.displayJLabel.x, altitudeJTextField.y, frame.displayJLabel.width, altitudeJTextField.height)
        coordinatesJTextField.bounds = Rectangle(frame.coordinatesJLabel.x, displayJTextField.y, frame.actualWidth - frame.coordinatesJLabel.x - 280, displayJTextField.height)

        frame.jPanel.add(altitudeJTextField)
        frame.jPanel.add(displayJTextField)
        frame.jPanel.add(coordinatesJTextField)
        frame.jPanel.preferredSize = Dimension(frame.actualWidth - 40, coordinatesJTextField.y + coordinatesJTextField.height + 60)
    }

    /** Adds the remove stock button for this line */
    fun addButton() {
        if (!removable) return
        removeButton.bounds = Rectangle(coordinatesJTextField.x + coordinatesJTextField.width + 20, coordinatesJTextField.y, 200, coordinatesJTextField.height)
        removeButton.addActionListener {
            SwingUtilities.invokeLater {
                frame.jPanel.remove(altitudeJTextField)
                frame.jPanel.remove(displayJTextField)
                frame.jPanel.remove(coordinatesJTextField)
                frame.jPanel.remove(removeButton)

                frame.jPanel.invalidate()
                frame.jPanel.repaint()

                frame.removeStockLine(this)
            }
        }
        frame.jPanel.add(removeButton)
    }

    /** Shifts everything in this line up by one line */
    fun shiftUpByOneLine() {
        altitudeJTextField.bounds = Rectangle(frame.altitudeJLabel.x, altitudeJTextField.y - 40, frame.altitudeJLabel.width, 25)
        displayJTextField.bounds = Rectangle(frame.displayJLabel.x, altitudeJTextField.y, frame.displayJLabel.width, altitudeJTextField.height)
        coordinatesJTextField.bounds = Rectangle(frame.coordinatesJLabel.x, displayJTextField.y, frame.actualWidth - frame.coordinatesJLabel.x - 280, displayJTextField.height)

        removeButton.bounds = Rectangle(coordinatesJTextField.x + coordinatesJTextField.width + 20, coordinatesJTextField.y, 200, coordinatesJTextField.height)
    }

    /** Sets the various fields to the input data */
    fun loadSaveData(altitude: String, display: String, labelX: String, labelY: String, coordinates: String) {
        altitudeJTextField.text = altitude
        displayJTextField.text = display
        coordinatesJTextField.text = coordinates
        this.labelX = labelX.toInt()
        this.labelY = labelY.toInt()
    }

    /** Replaces points with actual coordinates if present, returns the single-line string representation of this obstacle line to be used in saves */
    fun getSaveString(): String {
        val stringBuilder = StringBuilder()
        for (point: String in coordinatesJTextField.text.split(", ")) {
            if (stringBuilder.isNotEmpty()) stringBuilder.append(", ")
            val pointData = frame.pointMap[point]
            if (pointData == null) {
                stringBuilder.append(point)
            } else stringBuilder.append(pointData[0]).append(", ").append(pointData[1])
        }
        coordinatesJTextField.text = stringBuilder.toString()
        return altitudeJTextField.text.replace(",", "") + ", " + displayJTextField.text.replace(",", "") + ", $labelX, $labelY, " + coordinatesJTextField.text
    }
}