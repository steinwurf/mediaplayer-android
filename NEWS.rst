News for mediaplayer-android
============================

This file lists the major changes between versions. For a more detailed list of
every change, see the Git log.

Latest
------
* Minor: SampleStorage now takes `ByteBuffer` and `Sample` as well as `byte[]`.
* Minor: AudioDecoder::build can no longer return null.

7.0.0
-----
* Major: Use androidGitVersion for setting the version of the apps and
  libraries automatically.
* Major: Bump version to 7 since the library version follows the git tag (and
  the latest version was 6.0.0).
