import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


fun main(args: Array<String>) {
    val urls = hashSetOf(
            "https://raw.githubusercontent.com/JavaBy/jprof-hugo/master/content/about.md",
            "https://raw.githubusercontent.com/JavaBy/jprof-hugo/master/content/contact.md")

    val download = Pattern.compile("\"download_url\":\"(.*?)\"")

    forEachMatched("https://api.github.com/repos/JavaBy/jprof-hugo/contents/content/post", download) { matcher ->
        urls.add(matcher.group(1))
    }

    val word = Pattern.compile("java", Pattern.CASE_INSENSITIVE)

    val total = urls.stream().map { url ->
        var count = 0
        forEachMatched(url, word) { count++ }
        count
    }.parallel().reduce(0, Integer::sum)

    println(total)
}

fun forEachMatched(url: String, pattern: Pattern, block: (Matcher) -> Unit) = URL(url).openConnection().inputStream.reader().forEachLine {
    val matcher = pattern.matcher(it)
    while (matcher.find()) block(matcher)
}