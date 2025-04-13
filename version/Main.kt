import java.io.File

fun main() {
    val VERSION_FILE = "VERSION.txt"

    val file = File(VERSION_FILE)
    if (!file.exists()) file.createNewFile()
    val text = file.readText().trim()
    val num = "%04d".format(text.toInt() + 1)

    val old = File("../codebase.jar")
    val new = File("../codebase-v$num.jar")
    old.renameTo(new)

    file.writeText("$num")

    println("done, new codebase compiled to codebase-v$num.jar")
}
