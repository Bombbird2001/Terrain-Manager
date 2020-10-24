import javax.swing.JOptionPane

class TerrainManager {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val frame = Frame("Terrain Manager")
            frame.loadLabels()
            frame.loadSave()
            frame.loadButtons()
            JOptionPane.showMessageDialog(null, "Welcome to terrain manager!")
        }
    }
}