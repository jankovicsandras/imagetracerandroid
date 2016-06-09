# imagetracerandroid
![alt Bitmap to Svg](docimages/s1.png)
Simple raster image tracer and vectorizer written in Java for Android. See https://github.com/jankovicsandras/imagetracerjava for the desktop Java version.

by András Jankovics 2015, 2016

This is the Android version of ImageTracer.java, please read that for more details: https://github.com/jankovicsandras/imagetracerjava

The library:
imagetracerandroid/src/jankovicsandras/imagetracerandroid/ImageTracerAndroid.java

Sample Activity:
imagetracerandroid/src/jankovicsandras/imagetracerapp/ImagetracerActivity.java

Android application:
imagetracerandroid/bin/ImageTracerApp.apk

The whole package is an Android project.

I will try to update this documentation as soon as I have enough time. :)

### 1.1.1

- Bugfix: CSS3 RGBA output in SVG was technically incorrect (however supported by major browsers), so this is changed. [More info](https://stackoverflow.com/questions/6042550/svg-fill-color-transparency-alpha)
- transparency support: alpha is not discarded now, it is given more weight in color quantization
- new options.roundcoords : rounding coordinates to a given decimal place. This can reduce SVG length significantly (>20%) with minor loss of precision.
- new options.desc : setting this to false will turn off path descriptions, reducing SVG length.
- new options.viewbox : setting this to true will use viewBox instead of exact width and height
- new options.colorsampling : color quantization will sample the colors now by default, can be turned off.
- new options.blurradius : setting this to 1..5 will preprocess the image with a selective Gaussian blur with options.blurdelta treshold. This can filter noise and improve quality.
- IndexedImage has width and height
- getsvgstring() needs now only IndexedImage (tracedata) and options as parameters
- colorquantization() needs now only imgd, palette and options as parameters
- background field is removed from the results of color quantization 

### Options
|Option name|Default value|Meaning|
|-----------|-------------|-------|
|ltres|1|Error treshold for straight lines.|
|qtres|1|Error treshold for quadratic splines.|
|pathomit|8|Edge node paths shorter than this will be discarded for noise reduction.|
|blurradius|0|Set this to 1..5 for selective Gaussian blur preprocessing.|
|blurdelta|20|RGBA delta treshold for selective Gaussian blur preprocessing.|
|numberofcolors|16|Number of colors to use on palette if pal object is not defined.|
|mincolorratio|0.02|Color quantization will randomize a color if fewer pixels than (total pixels*mincolorratio) has it.|
|colorquantcycles|3|Color quantization will be repeated this many times.|
|scale|1|Every coordinate will be multiplied with this, to scale the SVG.|
|colorsampling|1|Enable or disable color sampling. 1 is on, 0 is off.|
|viewbox|0|Enable or disable SVG viewBox. 1 is on, 0 is off.|
|desc|1|Enable or disable SVG descriptions. 1 is on, 0 is off.|
|lcpr|0|Straight line control point radius, if this is greater than zero, small circles will be drawn in the SVG. Do not use this for big/complex images.|
|qcpr|0|Quadratic spline control point radius, if this is greater than zero, small circles and lines will be drawn in the SVG. Do not use this for big/complex images.|

### Process overview
See [Process overview and Ideas for improvement](https://github.com/jankovicsandras/imagetracerandroid/blob/master/process_overview.md)

### License
#### The Unlicense / PUBLIC DOMAIN

This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to [http://unlicense.org](http://unlicense.org)
