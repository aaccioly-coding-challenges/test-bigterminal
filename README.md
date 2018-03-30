# test-bigterminal

Think of the canonical coding challenge for new students where, given a paragraph, you create a program that keeps count of each instance of a word in the paragraph -- this will be a variation on that theme.

In either Java or Scala (whichever you are most comfortable with), write a generic & thread safe custom collection that will be used for counting any type of object using *any* numeric type (ie. `Int`, `Double`, *`BigDecimal`) and maintains a mapping of `Any -> Numeric`. This collection will only need 3 methods with the following signatures:

    void add(key, amount)
    void subtract(key, amount)
    T get(key)

The collection should be able to increment and decrement a key's count by arbitrary amounts of any numeric type without losing precision.

When the class is instantiated it should take an optional default value otherwise counts will start at zero. You shouldn't have to put a key into the collection, adding or subtracting with a key should put it in if its absent.

** This collection should be 100% atomic and thread safe.

### Assumptions

1. It's ok to use an underlying collection.
2. Safe / well behaved keys only.
3. Don’t document your code. Code your documentation.
4. Coding exercises are like Poetry. I wouldn't take the same artistic license otherwise