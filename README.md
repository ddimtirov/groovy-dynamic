[![Dependency Status](https://www.versioneye.com/user/projects/53bb867c609ff088d400005d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/53bb867c609ff088d400005d)

@Dynamic annotation for Groovy
==============================

While by default Groovy resolves the methods to invoke dynamically, it still enforces the declared types by 
inserting type-checks on each ambiguous assignment operation. While that is usually a cheap and useful check,
there is a case where it is desirable to disable it and fall back to pure dynamic invocation.
   
Whenever we need to deal with classes loaded from multiple classloaders (typically done by runtimes to provide 
segregated static domains), having the types cast to the actual type loaded by the main classloader inevitably 
results in `ClassCastException`. Sometimes, in test cases we need to manipulate such classes in order to set up 
our fixture or assert state. This is easily done if we define the variable as untyped (i.e. `def foobar`) - this
allows us to call the methods and access the fields, but at the cost that the code becomes untyped and we get no
tool support from IDE's (i.e. code completion, navigation, warnings).

This library provides an alternative approach - annotate references or methods with `@Dynamic` and it would apply
a type transformation, stripping the type at compile time, so that for all practical reasons the code will be typed, 
but the actual values will not be casted.

For example instead of:

```groovy
def instance = lookupObjectFromDifferentClassloader(MyType)
def value = instance.getSomeValue().copy()
value.setFlag(true)
instance.setSomeValue(value)
```

It allows you to write:

```groovy
@Dynamic MyType instance = lookupObjectFromDifferentClassloader(MyType)
@Dynamic ValueType value = instance.getSomeValue().copy()
value.setFlag(true)
instance.setSomeValue(value)
```

Status
------

* variable initialization - DONE
* variable assignment - BROKEN
* field initialization - TODO
* field assignment - TODO
* parameter passing - TODO
* parameter assignment - TODO
* auto-marking all variables in annotated method - TODO
* auto-marking only variables of enumerated types in annotated method - TODO
 
