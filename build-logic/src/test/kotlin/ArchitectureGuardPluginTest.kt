import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ArchitectureGuardPluginTest {

    @Test
    fun `plugin registers project governance tasks`() {
        val project = ProjectBuilder.builder().withName("ToolKit").build()

        project.pluginManager.apply(ArchitectureGuardPlugin::class.java)

        assertNotNull(project.tasks.findByName("generateProjectIndex"))
        assertNotNull(project.tasks.findByName("verifyProjectIndex"))
        assertNotNull(project.tasks.findByName("verifyArchitecture"))
        assertEquals("documentation", project.tasks.getByName("generateProjectIndex").group)
        assertEquals("verification", project.tasks.getByName("verifyProjectIndex").group)
        assertEquals("verification", project.tasks.getByName("verifyArchitecture").group)
    }
}
