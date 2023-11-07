import kotlin.test.Test

class GitTest {
    private var git: Git? = null
    private var tree: Tree? = null

    @Test
    fun testInit() {
        git = Git()
        assert(git != null)

        tree = Tree()
        assert(tree != null)
    }
    @Test
    fun testCommit() {
        git = git ?: Git()
        tree = tree ?: Tree()

        tree!!.entries["file.txt"] = Either.Left(Blob("Hello, world!"))
        git!!.commit(tree!!, "John Doe", "Initial commit")

        val commits = git!!.listCommits()
        assert(commits.size == 1)
        assert(commits[0].author == "John Doe")
        assert(commits[0].message == "Initial commit")
        assert(tree == commits[0].tree)
    }

    @Test
    fun testFindCommitByHash() {
        git = git ?: Git()
        tree = tree ?: Tree()

        tree!!.entries["file.txt"] = Either.Left(Blob("Hello, world!"))
        git!!.commit(tree!!, "John Doe", "Initial commit")

        val commits = git!!.listCommits()
        val commit = git!!.findCommitByHash(commits[0].hash)
        assert(commit != null)
        assert(commit == commits[0])
    }

    @Test
    fun testTreeCommit() {
        git = git ?: Git()
        tree = tree ?: Tree()

        val commitTree = Tree()
        commitTree.entries["file.txt"] = Either.Left(Blob("Hello, world!"))

        tree!!.entries["file.txt"] = Either.Right(commitTree)
        git!!.commit(tree!!, "John Doe", "Initial commit")

        val commits = git!!.listCommits()
        assert(commits.size == 1)
        assert(commits[0].author == "John Doe")
        assert(commits[0].message == "Initial commit")
        assert(tree == commits[0].tree)
    }
}