package com.example

import com.example.ui.screens.student.formatSkillTag
import com.example.ui.screens.student.normalizeGitHubUsername
import com.example.ui.screens.student.normalizeTeamRepoInput
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNormalizeGitHubUsername() {
    assertEquals("torvalds", normalizeGitHubUsername("https://github.com/torvalds/"))
    assertEquals("torvalds", normalizeGitHubUsername("http://github.com/torvalds/linux"))
    assertEquals("octocat", normalizeGitHubUsername("@octocat"))
    assertEquals("user123", normalizeGitHubUsername("github.com/user123/"))
    assertEquals("my-user", normalizeGitHubUsername("  my-user  "))
  }

  @Test
  fun testFormatSkillTag() {
    assertEquals("Backend", formatSkillTag("backend"))
    assertEquals("Frontend", formatSkillTag("frontend"))
    assertEquals("Data", formatSkillTag("data"))
    assertEquals("AI/ML", formatSkillTag("ai_ml"))
    assertEquals("DevOps", formatSkillTag("devops"))
    assertEquals("Security", formatSkillTag("security"))
  }

  @Test
  fun testNormalizeTeamRepoInput() {
    assertEquals("facebook/react", normalizeTeamRepoInput("https://github.com/facebook/react/tree/main"))
    assertEquals("owner/repo", normalizeTeamRepoInput("github.com/owner/repo/"))
    assertEquals("owner/repo", normalizeTeamRepoInput("@owner/repo"))
    assertEquals("owner/repo", normalizeTeamRepoInput("owner/repo"))
  }
}
