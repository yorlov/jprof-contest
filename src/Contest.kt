import java.io.BufferedReader
import java.net.URL
import java.util.regex.Pattern


fun main(args: Array<String>) {
    val pattern = Pattern.compile("\"download_url\":\"(.*?)\"")

    val urls = hashSetOf(
            "https://raw.githubusercontent.com/JavaBy/jprof-hugo/master/content/about.md",
            "https://raw.githubusercontent.com/JavaBy/jprof-hugo/master/content/contact.md")

    reader("https://api.github.com/repos/JavaBy/jprof-hugo/contents/content/post") {
        val matcher = pattern.matcher(readLine())
        while (matcher.find()) {
            urls.add(matcher.group(1))
        }
    }

    val word = Pattern.compile("java", Pattern.CASE_INSENSITIVE)

    val total = urls.stream().map { url ->
        var count = 0
        reader(url) {
            var line = readLine()
            while (line != null) {
                val matcher = word.matcher(line)
                while (matcher.find()) {
                    count++
                }
                line = readLine()
            }
        }
        count
    }.parallel().reduce(0, Integer::sum)

    println(total)
}

fun reader(url: String, block: BufferedReader.() -> Unit) = URL(url).openConnection().inputStream.bufferedReader().use(block)