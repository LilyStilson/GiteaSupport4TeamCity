import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GitTest {
    @Test
    fun testCommit() {
        val git = Git()
        val tree = Tree()

        git.addFiles(tree, mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))
        git.commit(tree, "Jan Novak", "Initial commit")


        val commits = git.listCommits()
        assert(commits.size == 1)
        assert(commits[0].author == "Jan Novak")
        assert(commits[0].message == "Initial commit")
        assert(tree == commits[0].tree)
    }

    @Test
    fun testTreeCommit() {
        val git = Git()
        val tree = Tree()

        val commitTree = Tree()

        git.addFiles(tree, mapOf("file.txt" to Either.Right(commitTree)))
        git.commit(tree, "Jan Novak", "Initial commit")

        val commits = git.listCommits()
        assert(commits.size == 1)
        assert(commits[0].author == "Jan Novak")
        assert(commits[0].message == "Initial commit")
        assert(tree == commits[0].tree)
    }

    @Test
    fun testCommitWithPrivateTree() {
        val git = Git()

        git.addFiles(files = mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))
        git.commit(author = "Jan Novak", message = "Initial commit")

        val commits = git.listCommits()
        assert(commits.size == 1)
        assert(commits[0].author == "Jan Novak")
        assert(commits[0].message == "Initial commit")
    }

    @Test
    fun testTwoCommitsWithPrivateTree() {
        val git = Git()

        git.addFiles(files = mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))
        git.commit(author = "Jan Novak", message = "Initial commit")

        git.addFiles(files = mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))
        git.commit(author = "Jan Novak", message = "2nd commit")

        val commits = git.listCommits()
        assert(commits.size == 2)
        assert(commits[0].author == "Jan Novak")
        assert(commits[0].message == "Initial commit")
        assert(commits[1].author == "Jan Novak")
        assert(commits[1].message == "2nd commit")
    }

    @Test
    fun testHashes() {
        val blob1 = Blob("Hello, world!")
        val blob2 = Blob("Hello, world!")
        assert(blob1.hash == blob2.hash)

        val tree1 = Tree()
        tree1.entries["file.txt"] = Either.Left(blob1)
        val tree2 = Tree()
        tree2.entries["file.txt"] = Either.Left(blob2)
        assert(tree1.hash == tree2.hash)
    }

    @Test
    fun testCatchEmptyTree() {
        val git = Git()

        assertThrows<IllegalStateException> {
            git.commit(Tree(), "Jan Novak", "Initial commit")
        }

        assertThrows<IllegalStateException> {
            git.commit(author = "Jan Novak", message = "Initial commit")
        }
    }

    @Test
    fun testFindCommit1() {
        val git = Git()

        git.addFiles(files = mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))
        git.commit(author = "Jan Novak", message = "Initial commit")

        val commits = git.listCommits()
        val commit = git.findCommit { it.hash == commits[0].hash }
        assert(commit != null)
        assert(commit == commits[0])
    }

    @Test
    fun testFindCommit2() {
        val git = Git()

        for (i in 1..3) {
            git.addFiles(
                files = mapOf(
                    "file.txt" to Either.Left(Blob("Hello, world${"!".repeat(i)}")),
                    "file2.txt" to Either.Left(Blob("Hello, world${"!".repeat(i)}")),
                )
            )
            git.commit(author = "Jan Novak", message = "$i commit")
        }

        git.addFiles(files = mapOf(
            "blob1" to Either.Left(Blob("Hello, world!")),
        ))
        git.commit(author = "Konstantin R.", message = "What?")

        val search = git.findCommits { it.author == "Jan Novak" }
        assert(search.size == 3)

        val search2 = git.findCommit { it.message == "What?" }
        assert(search2 != null)
        assert(search2!!.author == "Konstantin R.")
    }
}