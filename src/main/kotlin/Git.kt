package org.nightskystudio.git

import java.security.MessageDigest
import java.time.LocalDateTime

/**
 * Calculates hash for given string
 * @param data String to calculate hash for
 * @return SHA1 hash of given string
 */
fun calcHash(data: String): String =
    MessageDigest.getInstance("SHA-1")
        .digest(data.toByteArray())
        .joinToString("") { "%02x".format(it) }

/**
 * Git exception wrapper
 */
class GitException(message: String) : Exception(message)

/**
 * Represents either left or right value
 * @param A Type of left value
 * @param B Type of right value
 */
sealed class Either<out A, out B> {
    data class Left<out A>(val a: A) : Either<A, Nothing>()
    data class Right<out B>(val b: B) : Either<Nothing, B>()
}

/**
 * Represents blob object
 * @constructor Creates blob object and calculates its hash
 * @param data Data of blob
 */
data class Blob(val data: String) {
    val hash = calcHash(data)
}

/**
 * Represents tree object
 * @constructor Creates tree object and calculates its hash
 * @param entries Entries of tree
 */
data class Tree(val entries: MutableMap<String, Either<Blob, Tree>> = mutableMapOf()) {
    val hash = calcHash(entries.toString())
}

/**
 * Represents commit object
 * @constructor Creates commit object, sets commit time and hash
 * @param tree Tree of data
 * @param author Commit author
 * @param message Commit message
 * @param time Timestamp
 * @property hash Hash of commit
 *
 */
data class Commit(val tree: Tree, val author: String, val message: String,
                  val time: LocalDateTime = LocalDateTime.now()) {
    val hash = calcHash("$tree$author$message$time")
}

/**
 * Simple git implementation
 * @constructor Creates git object
 * @property commits List of commits
 * @property commitTree Tree of commits
 */
class Git {
    private val commits = mutableListOf<Commit>()

    /**
     * Unless tree is not specified in addFiles and commit methods,
     * this tree will be used
     */
    private var commitTree: Tree? = null

    /**
     * Checks if files are already in tree
     * @param tree Tree to check
     * @param files Map of files to check
     * @return True if files are in tree, false otherwise
     */
    private fun filesAreInTree(tree: Tree, files: Map<String, Either<Blob, Tree>>): Boolean {
        val sameKey = tree.entries.any { files.containsKey(it.key) }
        val sameBlob = tree.entries.values.any { files.containsValue(it) }
        return sameKey && sameBlob
    }

    /**
     * Adds files to commit tree
     * @param tree Tree to add files to
     * @param files Map of files to add
     * @throws GitException if tree already exists in commits
     */
    fun addFiles(tree: Tree? = null, files: Map<String, Either<Blob, Tree>>) {
        commitTree = tree ?: commitTree ?: Tree()
        if (filesAreInTree(commitTree!!, files))
            throw GitException("No changes were detected or this item already exists.")
        commitTree!!.entries.putAll(files)
    }

    /**
     * Commits changes
     * @param tree Tree to commit
     * @param author Author of commit
     * @param message Commit message
     * @return Commit object
     * @throws GitException if tree is empty
     */
    fun commit(tree: Tree? = null, author: String, message: String) {
        if (commitTree == null) commitTree = tree ?: Tree()

        if (commitTree!!.entries.isEmpty())
            throw GitException("Tree is empty, nothing to commit.")

        commits.add(Commit(commitTree!!, author, message))
        commitTree = null
    }

    /**
     * Lists all commits
     * @return List of commits
     */
    fun listCommits() = commits

    /**
     * Finds latest commit by predicate
     * @param predicate Search predicate
     * @return Commit object or null if not found
     */
    fun findCommit(predicate: (Commit) -> Boolean) = commits.sortedBy { it.time }.find(predicate)

    /**
     * Finds all commits by predicate
     * @param predicate Search predicate
     * @return List of commits
     */
    fun findCommits(predicate: (Commit) -> Boolean) = commits.filter(predicate)
}