# Java Hyper Text Preprocessor (JHTPP)

Really basic Hyper Text Preprocessor made in Java.

# Table of Contents

1. [Authors](#authors)
2. [How to use the example](#how-to-use-the-example)
3. [How to use the library](#how-to-use-the-library)
    1. [In Java](#in-java)
    2. [In HTML](#in-html)
4. [Example](#example)

## Authors

- [@anaselmo](https://github.com/anaselmo)
- [@yarasatomic](https://github.com/yarasatomic)

## How to use the example

### Compile with

```bash
./compile.sh
```

### Execute with

```bash
./execute.sh
```

For now, we pass parameters through the main class (it will be changed)

## How to use the library

### In Java:
You have to define a `VarTree` where you will have to define the different variables and subvariables.

```Java
VarTree a = new VarTree();
```

You can put in `a` either:
- A `key` and a `value`:
    ```Java
    a.put("key", "value");
    ```
- A `key` and another `VarTree`:
    ```Java
    VarTree b = new VarTree();
    a.put("key", b);
    ```
Now, to use the JHTPP class you have to define a `JHTPP` variable:
- Using the path of the file stored in a `String`:
    ```Java
    JHTTP processor = new JHTTP("file", str, varTree);
    ```
- Using the HTML content stored in a `String`:
    ```Java
    JHTTP processor = new JHTTP("text", str, varTree);
    ```
Being `str` a String and `varTree` a VarTree.

### In HTML:

To represent a variable we use the next syntaxis: 
```
{{var}}
```
If you defined in the VarTree a key `var` with value `hello`, the output would be:
```
hello
```
We can access the attributes of the variable:
```
{{var.attribute}}
```

To iterate through all the attributes of a variable, we can use a `for loop` : 
```
{% for d in days %}
{{d}}
{% endfor d %}
```
If you defined in the VarTree a key `days` with value as a VarTree with `all the days of the week`, the output would be:
```
Monday
Tuesday
Wednesday
Thrusday
Friday
Saturday
Sunday
```

Nested loops are possible.

## Example

### Input file:

```html
<!DOCTYPE html>
<html>
<body>
    <h1>Hello, {{name}}!</h1>
    <p>Welcome to my web, {{name2}}!</p>
    
    {% for d1 in days %}
    {{d1.name}}
    {%endfor d1 %}

</body>
</html>
```

### Output file:

```html
<!DOCTYPE html>
<html>
<body>
    <h1>Hello, YarasAtomic!</h1>
    <p>Welcome to my web, Anaselmo!</p>
    
    Monday  
    Tuesday
    Wednesday
    Thrusday
    Friday
    Saturday
    Sunday
    
</body>
</html>
```