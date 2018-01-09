import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors.toSet
import java.util.stream.Stream
import java.util.stream.StreamSupport


fun main(args: Array<String>) {
    val download = Pattern.compile("<link>(.*?)</link>")
    val word = Pattern.compile("java", Pattern.CASE_INSENSITIVE)

    val urls = matchedStream("https://jprof.by/index.xml", download) { matcher -> matcher.group(1) }.collect(toSet())
    val total = urls.stream().flatMap { url -> matchedStream(url, word) { 1 } }.parallel().reduce(0, Integer::sum)

    println(total)
}

fun <T : Any> matchedStream(url: String, pattern: Pattern, block: (Matcher) -> T): Stream<T> =
        URL(url).openConnection().apply {
            addRequestProperty("User-Agent", "Y")
        }.inputStream.bufferedReader().lines().flatMap { line ->
            val matcher = pattern.matcher(line)
            val iterable = object : Iterable<T> {
                override fun iterator() = object : Iterator<T> {
                    override fun hasNext() = matcher.find()
                    override fun next() = block(matcher)
                }
            }
            StreamSupport.stream(iterable.spliterator(), false)
        }