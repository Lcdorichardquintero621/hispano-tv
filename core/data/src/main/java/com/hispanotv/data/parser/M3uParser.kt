package com.hispanotv.data.parser

import com.hispanotv.domain.model.Channel
import com.hispanotv.domain.model.Country
import kotlinx.serialization.Serializable

object M3uParser {

    @Serializable
    data class M3uChannel(
        val tvgId: String?,
        val tvgName: String?,
        val tvgLogo: String?,
        val groupTitle: String?,
        val tvgCountry: String?,
        val tvgLanguage: String?,
        val tvgUrl: String?,
        val tvgShift: String?,
        val radio: Boolean?,
        val url: String
    )

    @Serializable
    data class ParsedM3u(
        val channels: List<M3uChannel>,
        val countries: Map<String, Int>
    )

    private const val EXTINF_PREFIX = "#EXTINF:"
    private const val EXTM3U = "#EXTM3U"

    fun parse(m3uContent: String): ParsedM3u {
        val lines = m3uContent.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val channels = mutableListOf<M3uChannel>()
        val countryCounts = mutableMapOf<String, Int>()

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.startsWith(EXTINF_PREFIX)) {
                val extinfLine = line.substring(EXTINF_PREFIX.length)
                val urlIndex = i + 1
                if (urlIndex < lines.size) {
                    val urlLine = lines[urlIndex]
                    if (urlLine.startsWith("http") || urlLine.startsWith("rtmp") || urlLine.startsWith("rtsp")) {
                        val channel = parseExtInfLine(extinfLine, urlLine)
                        channels.add(channel)
                        if (channel.tvgCountry != null) {
                            val country = channel.tvgCountry!!.uppercase()
                            countryCounts[country] = countryCounts.getOrDefault(country, 0) + 1
                        }
                    }
                }
                i += 2
            } else {
                i++
            }
        }

        return ParsedM3u(channels, countryCounts.toMap())
    }

    private fun parseExtInfLine(extinfLine: String, url: String): M3uChannel {
        val attributes = mutableMapOf<String, String>()
        var name = ""
        var inQuotes = false
        var currentKey = ""
        var currentValue = ""
        var parsingKey = true
        var i = 0

        while (i < extinfLine.length) {
            val char = extinfLine[i]
            when {
                char == ' ' && !inQuotes && parsingKey && currentKey.isNotEmpty() -> {
                    parsingKey = false
                }
                char == '=' && !inQuotes && !parsingKey -> {
                    inQuotes = true
                }
                char == '"' && inQuotes -> {
                    inQuotes = false
                    attributes[currentKey] = currentValue
                    currentKey = ""
                    currentValue = ""
                    parsingKey = true
                }
                inQuotes -> {
                    currentValue += char
                }
                char == ',' && !inQuotes -> {
                    name = extinfLine.substring(i + 1).trim()
                    break
                }
                else -> {
                    if (parsingKey) {
                        currentKey += char
                    }
                }
            }
            i++
        }

        if (name.isEmpty() && attributes.containsKey("tvg-name")) {
            name = attributes["tvg-name"]!!
        } else if (name.isEmpty()) {
            name = attributes["tvg-id"] ?: "Unknown"
        }

        return M3uChannel(
            tvgId = attributes["tvg-id"],
            tvgName = attributes["tvg-name"],
            tvgLogo = attributes["tvg-logo"],
            groupTitle = attributes["group-title"],
            tvgCountry = attributes["tvg-country"],
            tvgLanguage = attributes["tvg-language"],
            tvgUrl = attributes["tvg-url"],
            tvgShift = attributes["tvg-shift"],
            radio = attributes["radio"]?.toBooleanOrNull(),
            url = url.trim()
        )
    }

    fun toDomainChannels(m3uChannels: List<M3uChannel>): List<Channel> {
        return m3uChannels.map { m3u ->
            val category = parseCategory(m3u.groupTitle)
            val countryCode = m3u.tvgCountry?.uppercase() ?: "XX"

            Channel(
                id = m3u.tvgId ?: m3u.url.hashCode().toString(),
                name = m3u.tvgName ?: "Unknown",
                logoUrl = m3u.tvgLogo?.takeIf { it.startsWith("http") },
                streamUrl = m3u.url,
                countryCode = countryCode,
                category = category
            )
        }
    }

    private fun parseCategory(groupTitle: String?): Channel.ChannelCategory {
        val group = groupTitle?.lowercase() ?: ""
        return when {
            group.contains("news", true) || group.contains("noticia", true) -> Channel.ChannelCategory.NEWS
            group.contains("sport", true) || group.contains("deport", true) -> Channel.ChannelCategory.SPORTS
            group.contains("movie", true) || group.contains("pelicul", true) || group.contains("cine", true) -> Channel.ChannelCategory.MOVIES
            group.contains("kids", true) || group.contains("infantil", true) || group.contains("cartoon", true) || group.contains("disney", true) -> Channel.ChannelCategory.KIDS
            group.contains("music", true) || group.contains("musica", true) || group.contains("mtv", true) -> Channel.ChannelCategory.MUSIC
            group.contains("document", true) || group.contains("docu", true) -> Channel.ChannelCategory.DOCUMENTARY
            group.contains("religi", true) || group.contains("iglesia", true) -> Channel.ChannelCategory.RELIGIOUS
            group.contains("shop", true) || group.contains("compras", true) -> Channel.ChannelCategory.SHOPPING
            group.contains("entreten", true) || group.contains("variedad", true) || group.contains("general", true) -> Channel.ChannelCategory.ENTERTAINMENT
            else -> Channel.ChannelCategory.GENERAL
        }
    }

    fun toDomainCountries(parsedM3u: ParsedM3u): List<Country> {
        val countryMap = mapOf(
            "AR" to "Argentina", "BO" to "Bolivia", "BR" to "Brasil", "CL" to "Chile",
            "CO" to "Colombia", "CR" to "Costa Rica", "CU" to "Cuba", "DO" to "República Dominicana",
            "EC" to "Ecuador", "SV" to "El Salvador", "ES" to "España", "GT" to "Guatemala",
            "HN" to "Honduras", "MX" to "México", "NI" to "Nicaragua", "PA" to "Panamá",
            "PY" to "Paraguay", "PE" to "Perú", "PR" to "Puerto Rico", "UY" to "Uruguay",
            "VE" to "Venezuela", "US" to "Estados Unidos"
        )

        val flagMap = mapOf(
            "AR" to "🇦🇷", "BO" to "🇧🇴", "BR" to "🇧🇷", "CL" to "🇨🇱",
            "CO" to "🇨🇴", "CR" to "🇨🇷", "CU" to "🇨🇺", "DO" to "🇩🇴",
            "EC" to "🇪🇨", "SV" to "🇸🇻", "ES" to "🇪🇸", "GT" to "🇬🇹",
            "HN" to "🇭🇳", "MX" to "🇲🇽", "NI" to "🇳🇮", "PA" to "🇵🇦",
            "PY" to "🇵🇾", "PE" to "🇵🇪", "PR" to "🇵🇷", "UY" to "🇺🇾",
            "VE" to "🇻🇪", "US" to "🇺🇸"
        )

        val popularCountries = setOf("MX", "ES", "AR", "CO", "CL", "PE", "US")

        return parsedM3u.countries.entries
            .filter { countryMap.containsKey(it.key) }
            .sortedByDescending { it.value }
            .map { entry ->
                Country(
                    code = entry.key,
                    name = countryMap[entry.key]!!,
                    flagEmoji = flagMap[entry.key] ?: "🏳️",
                    channelCount = entry.value,
                    isPopular = popularCountries.contains(entry.key)
                )
            }
    }
}