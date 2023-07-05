package chapter9;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;

import static java.lang.foreign.ValueLayout.*;

public class ForeignFuncMemAPIDemo {

    public static void main(String[] args) {
        // 1. Find foreign function on the C library path
        Linker linker          = Linker.nativeLinker();
        SymbolLookup stdlib    = linker.defaultLookup();
        MethodHandle radixsort = linker.downcallHandle(stdlib.find("radixsort").orElseThrow(),
                FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT, ADDRESS, JAVA_CHAR));
        // 2. Allocate on-heap memory to store four strings
        String[] javaStrings = { "mouse", "cat", "dog", "car" };
        // 3. Use try-with-resources to manage the lifetime of off-heap memory
        try (Arena offHeap = Arena.ofConfined()) {
            // 4. Allocate a region of off-heap memory to store four pointers
            MemorySegment pointers
                    = offHeap.allocateArray(ADDRESS, javaStrings.length);
            // 5. Copy the strings from on-heap to off-heap
            for (int i = 0; i < javaStrings.length; i++) {
                MemorySegment cString = offHeap.allocateUtf8String(javaStrings[i]);
                pointers.setAtIndex(ADDRESS, i, cString);
            }
            // 6. Sort the off-heap data by calling the foreign function
            radixsort.invoke(pointers, javaStrings.length, MemorySegment.NULL, '\0');
            // 7. Copy the (reordered) strings from off-heap to on-heap
            for (int i = 0; i < javaStrings.length; i++) {
                MemorySegment cString = pointers.getAtIndex(ADDRESS, i);
                javaStrings[i] = cString.getUtf8String(0);
            }
        } // 8. All off-heap memory is deallocated here
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        assert Arrays.equals(javaStrings,
                new String[] {"car", "cat", "dog", "mouse"});  // true
    }
}
