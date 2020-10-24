import java.awt.Dimension
import java.awt.Rectangle
import java.io.File
import java.lang.StringBuilder
import javax.swing.*

class Frame(title: String): JFrame(title) {
    val jPanel: JPanel
    private val jScrollPane: JScrollPane
    var actualWidth = 960
    private var actualHeight = 540
    private val obstacleList = ArrayList<ObstacleLine>()
    private lateinit var saveJButton: JButton
    private lateinit var addLineJButton: JButton
    lateinit var altitudeJLabel: JLabel
    lateinit var displayJLabel: JLabel
    lateinit var coordinatesJLabel: JLabel
    var pointMap = HashMap<String, FloatArray>()

    init {
        setSize(960, 540)
        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        layout = null
        actualWidth = bounds.width
        actualHeight = bounds.height - 110
        jPanel = JPanel()
        jPanel.layout = null
        jPanel.setSize(actualWidth - 40, actualHeight)
        jScrollPane = JScrollPane(jPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        jScrollPane.setSize(actualWidth - 20, actualHeight)
        jScrollPane.verticalScrollBar.unitIncrement = 10
        add(jScrollPane)
    }

    fun loadButtons() {
        saveJButton = JButton("Save obstacle data")
        saveJButton.bounds = Rectangle(actualWidth / 2 - 100, actualHeight + 10, 200, 50)
        saveJButton.addActionListener {
            val dataFile = File("new_output.txt")
            if (dataFile.exists()) {
                dataFile.forEachLine {
                    loadPointData(it)
                }
            }

            val stringBuilder = StringBuilder()
            for (obstacleLine in obstacleList) {
                if (stringBuilder.isNotEmpty()) stringBuilder.append("\n")
                stringBuilder.append(obstacleLine.getSaveString())
            }
            val file = File("obstacles_new.obs")
            if (!file.exists() && !file.createNewFile()) {
                JOptionPane.showMessageDialog(null, "Failed to save obstacle data")
                return@addActionListener
            }
            file.writeText(stringBuilder.toString())
            JOptionPane.showMessageDialog(null, "Obstacle data saved successfully!")
        }
        add(saveJButton)
        invalidate()
        repaint()

        addLineJButton = JButton("+ Add new obstacle")
        addLineJButton.bounds = Rectangle(50, 50 + obstacleList.size * 40, 200, 25)
        addLineJButton.addActionListener {
            SwingUtilities.invokeLater {
                //Must run in correct thread
                addLine(true)
                addLineJButton.bounds = Rectangle(50, addLineJButton.y + 40, 200, 25)
            }
        }
        jPanel.add(addLineJButton)

        jPanel.invalidate()
        jPanel.repaint()
    }

    fun loadLabels() {
        altitudeJLabel = JLabel("Altitude", SwingConstants.CENTER)
        altitudeJLabel.bounds = Rectangle(30, 20, 100, 25)
        jPanel.add(altitudeJLabel)

        displayJLabel = JLabel("Display", SwingConstants.CENTER)
        displayJLabel.bounds = Rectangle(altitudeJLabel.x + altitudeJLabel.width + 20, altitudeJLabel.y, 100, altitudeJLabel.height)
        jPanel.add(displayJLabel)

        coordinatesJLabel = JLabel("Coordinates", SwingConstants.CENTER)
        coordinatesJLabel.bounds = Rectangle(displayJLabel.x + displayJLabel.width + 20, displayJLabel.y, 100, displayJLabel.height)
        jPanel.add(coordinatesJLabel)

        jPanel.invalidate()
        jPanel.repaint()
    }

    fun loadSave() {
        val file = File("obstacle.obs")
        if (!file.exists()) {
            //If no save file, start with one blank line
            addLine(false)
            return
        }
        var firstLineAdded = false
        file.forEachLine {
            addLine(firstLineAdded)
            if (!firstLineAdded) firstLineAdded = true
            val data = it.split(", ", limit = 5)
            obstacleList[obstacleList.size - 1].loadSaveData(data[0], data[1], data[2], data[3], data[4])
        }
    }

    private fun addLine(removable: Boolean) {
        val newLine = ObstacleLine(this, removable)
        obstacleList.add(newLine)
        newLine.addBoxes(obstacleList.size - 1)
        newLine.addButton()

        jPanel.invalidate()
        jPanel.repaint()

        jScrollPane.revalidate()
    }

    fun removeStockLine(obstacleLine: ObstacleLine) {
        val index = obstacleList.indexOf(obstacleLine)
        if (index > 0) {
            addLineJButton.bounds = Rectangle(50, addLineJButton.y - 40, 200, 25)
            obstacleList.removeAt(index)
            shiftLinesDown(index)
        }
    }

    private fun shiftLinesDown(startIndex: Int) {
        for (i in startIndex until obstacleList.size) {
            obstacleList[i].shiftUpByOneLine()
        }

        jPanel.preferredSize = Dimension(actualWidth - 40, jPanel.preferredSize.height - 40)

        jPanel.invalidate()
        jPanel.repaint()

        jScrollPane.revalidate()
    }

    private fun loadPointData(line: String) {
        val data = line.split(" ")
        //println("${data[0]} ${data[1]} ${data[2]}")
        pointMap[data[0]] = floatArrayOf(data[1].toFloat(), data[2].toFloat())
    }
}