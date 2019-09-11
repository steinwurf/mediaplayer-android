mediaplayer-android
===================

.. image:: https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat
    :target: https://android-arsenal.com/api?level=16

.. image:: https://jitpack.io/v/steinwurf/mediaplayer-android.svg?style=flat-square
    :target: https://jitpack.io/#steinwurf/mediaplayer-android

Android library for playback of raw data buffers as media. Currently AAC and H264 is
supported.

Usage
-----
To depend on mediaplayer-android you will need add the jitpack.io repository in the
project build.gradle file:

.. code-block::

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
            // ...
        }
    }

And then add the mediaplayer-android dependency to your module's dependencies scope:


.. code-block::

    dependencies {
        implementation 'com.github.steinwurf:mediaplayer-android:8.1.2'
    }
