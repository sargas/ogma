package net.neoturbine.ogma.libraries.skokie

import io.kotest.matchers.collections.shouldBeEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.UnsupportedOperationException

const val TEST_LIBRARY_CARD="21212121212"
const val TEST_PIN="1234"

@ExperimentalCoroutinesApi
class SkokieLibraryServiceTest {
    private val skokieLibraryService = SkokieLibraryService()
    private val server = MockWebServer()

    @BeforeEach
    fun `Start Server`() {
        server.dispatcher = SkokieDispatcher()
        server.start()
    }

    @Test
    fun `Happy path with no books`() = runBlockingTest {
        val listOfBooks = skokieLibraryService.getCheckedOutItems(TEST_LIBRARY_CARD, TEST_PIN)

        listOfBooks.shouldBeEmpty()
    }

    @AfterEach
    fun `Stop Server`() = server.shutdown()

    class SkokieDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse = when {
            isLoginRequest(request) -> loginResponse()
            isBorrowingListRequest(request) -> emptyBorrowingResponse()
            else -> throw UnsupportedOperationException()
        }

        private fun isBorrowingListRequest(request: RecordedRequest) =
            request.path?.endsWith("/user_stats/borrowing") == true

        private fun isLoginRequest(request: RecordedRequest) =
            (request.path?.endsWith("/user_dashboard") == true && request.headers.values("Set-Cookie")
                .isEmpty()
                    && request.body.readUtf8() == "name=$TEST_LIBRARY_CARD&user_pin=$TEST_PIN")

        private fun loginResponse() = MockResponse()
                .setResponseCode(302)
                .addHeader("Location: https://skokielibrary.bibliocommons.com/user_dashboard")
                .addHeader("Set-Cookie: agency_id=IL-SKOKIE; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: language=en-US; path=/; expires=Sun, 12-Apr-2026 01:36:44 GMT; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: view=medium; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: use_new_relic=true; domain=.bibliocommons.com; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: remember_me=; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: session_id=f9b7b10b-3b31-466e-ada1-654b012f9f05-1875982739; domain=.bibliocommons.com; path=/; HttpOnly; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: origin_domain=skokielibrary.bibliocommons.com; domain=.bibliocommons.com; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: bc_access_token=a04013d7-5605-4fac-85af-29a770b2546c; domain=.bibliocommons.com; path=/; HttpOnly; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: ugc_spoiler_visible=true; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: ugc_offensive_visible=true; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: users_followed=%5B%5D; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: users_ignored=%5B%5D; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: uniq_id=back_to_37cb2o5ei4hq1mb27hr1nuqu1o; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: elapsed_time=1057; path=/; SameSite=None; Secure; secure")
                .addHeader("Set-Cookie: _live_bcui_session_id=BAh7B0kiD3Nlc3Npb25faWQGOgZFRkkiJTk3ZGM3OGYwYjkxYWMzNDExZGI1NDRmYjA5YzVjOGRiBjsAVEkiCXVzZXIGOwBGewk6DmxvZ2dlZF9pbkkiCXRydWUGOwBGOgluYW1lSSINamoxMTA4ODgGOwBUOgxsaWJyYXJ5aQGFOgx1c2VyX2lkSSI0ZjliN2IxMGItM2IzMS00NjZlLWFkYTEtNjU0YjAxMmY5ZjA1LTE4NzU5ODI3MzkGOwBU--f71dc42a02961b81ebd805abc0ecee7edb30e54c; domain=.bibliocommons.com; path=/; secure; HttpOnly; SameSite=None; Secure")
                .addHeader("Set-Cookie: SRV=app03; path=/; domain=.bibliocommons.com; HttpOnly; Secure")
                .addHeader("X-Request-Id: 180a089c9c9bcd47c204fa2ac9606eee")
                .addHeader("X-Version: app03 Version 8.36.4 Last updated 2021/03/16 20:18")
                .addHeader("stats_bibliocommons: WS_MEM_ALL,9,6,1;RAILS,1057;http-bio-3000-exec-11")
                .setBody("""<html><body>You are being <a href="https://skokielibrary.bibliocommons.com/user_dashboard">redirected</a>.</body></html>""")

        private fun emptyBorrowingResponse() = MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type: application/json;charset=utf-8")
            .addHeader("Set-Cookie: elapsed_time=586; path=/; SameSite=None; Secure; secure")
            .addHeader("X-Version: app02 Version 8.36.4 Last updated 2021/03/16 20:18")
            .addHeader("stats_bibliocommons: WS_MEM_ALL,4,2,1;RAILS,586;http-bio-3000-exec-19")
            .setBody("""
                {
                    "checked_out": 0,
                    "coming_due": 0,
                    "enable_recently_returned": null,
                    "fines": "0.00",
                    "in_transit": null,
                    "library_messages": null,
                    "logged_in": true,
                    "messages": [],
                    "next_due": false,
                    "overdue": 0,
                    "ready": null,
                    "recently_returned_last_date_count": null,
                    "recently_returned_total_count": null,
                    "section": "checkedout",
                    "success": true,
                    "total_holds": null
                }""".trimIndent())
    }
}