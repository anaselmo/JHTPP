# Java Hyper Text Preprocessor (JHTPP)

Really basic Hyper Text Preprocessor made in Java.

# Table of Contents

- [Java Hyper Text Preprocessor (JHTPP)](#java-hyper-text-preprocessor-jhtpp)
- [Table of Contents](#table-of-contents)
  - [Authors](#authors)
  - [How to use the example](#how-to-use-the-example)
    - [Compile with](#compile-with)
    - [Execute with](#execute-with)
  - [How to use the library](#how-to-use-the-library)
    - [In Java:](#in-java)
    - [In HTML:](#in-html)
      - [var clauses:](#var-clauses)
      - [for clauses:](#for-clauses)
      - [if clauses:](#if-clauses)
  - [Example](#example)
    - [Input file:](#input-file)
    - [Output file:](#output-file)

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

> [!NOTE]
> For now, we pass parameters through the main class (it will be changed)

## How to use the library

### In Java:
You have to define a `VarTree` where you will have to define the different 
variables and subvariables.

```Java
VarTree varTree = new VarTree();
```

You can put in `varTree` either:
- A `key` and a `String`:
    ```Java
    varTree.put("key", "value");
    ```
- A `key` and another `VarTree`:
    ```Java
    VarTree otherVartTree = new VarTree();
    varTree.put("key", otherVartTree);
    ```
> [!NOTE]
> The key is a `String`

Now, to use the JHTPP class you have to define a `JHTPP` variable:
- Using the path of the file stored in a `String`:
    ```Java
    JHTTP processor = new JHTTP(InputType.PATH, path, varTree);
    ```
- Using the HTML content stored in a `String`:
    ```Java
    JHTTP processor = new JHTTP(InputType.CONTENT, content, varTree);
    ```
Being `path` and `content` a String and `varTree` a VarTree.

### In HTML:

#### var clauses:

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
#### for clauses:

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
> [!NOTE]
> Nested loops are possible.
> 
#### if clauses:

To create a `if structure` you can write for example:
```
{% if name != otherName %}
    '{{name}}' doesn't equal '{{otherName}}'
{% endif %}
```
And the output would be:
```
'Anaselmo' doesn't equal 'YarasAtomic'
```
> [!WARNING]
> For now we only have `==` and `!=`, and they have a really 
> basic functionality.  
> Implementation needs to be done


## Example

### Input file:

```html
<!DOCTYPE html>
<html>
<body>
    <h1>Hello, {{name}}!</h1>
    <p>Welcome to my web, {{otherName}}!</p>

    ===============================================
    
    {% for d in days %}
    {{d.name}}
        {% for ex in d.exercises %}
        {{ex}}
        {% endfor %}
    {% endfor %}

    ===============================================

    {% if name != otherName %}
        '{{name}}' doesn't equal '{{otherName}}'
    {% endif %}

</body>
</html>
```

### Output file:

```html
<!DOCTYPE html>
<html>
<body>
    <h1>Hello, Anaselmo!</h1>
    <p>Welcome to my web, YarasAtomic!</p>

    ===============================================
    
    Monday
        Chest
        Back

    Tuesday
        Biceps
        Triceps
        Shoulder

    Wednesday
        Leg
        
    Thursday
        Chest
        Back

    Friday
        Biceps
        Triceps
        Shoulder

    Saturday
        Leg

    Sunday
        Rest!

    ===============================================

    'Anaselmo' doesn't equal 'YarasAtomic'
    
</body>
</html>
```