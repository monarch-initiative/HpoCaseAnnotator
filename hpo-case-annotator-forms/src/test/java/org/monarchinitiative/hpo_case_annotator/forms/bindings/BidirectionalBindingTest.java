package org.monarchinitiative.hpo_case_annotator.forms.bindings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BidirectionalBindingTest {

    private ObjectProperty<Animal> animal;
    private ObjectProperty<Sound> sound;
    private BidirectionalBinding<Animal, Sound> binding;

    @BeforeEach
    public void setUp() {
        animal = new SimpleObjectProperty<>(this, "animal");
        sound = new SimpleObjectProperty<>(this, "sound");
        binding = new BidirectionalBinding<>(animal, sound, createAnimalToSound(), createSoundToAnimal());
    }

    private static Function<Sound, Animal> createSoundToAnimal() {
        return sound -> {
            if (sound == null)
                return null;
            else
                return switch (sound) {
                    case MEOW -> Animal.CAT;
                    case BARK -> Animal.DOG;
                    case TWEET -> Animal.BIRD;
                    case DUBIDIDU -> Animal.ELK;
                };
        };
    }

    private static Function<Animal, Sound> createAnimalToSound() {
        return animal -> {
            if (animal == null)
                return null;
            else
                return switch (animal) {
                    case BIRD -> Sound.TWEET;
                    case CAT -> Sound.MEOW;
                    case DOG -> Sound.BARK;
                    case ELK -> Sound.DUBIDIDU;
                };
        };
    }

    @Test
    @Disabled // TODO - this does not work at the moment
    public void wasGarbageCollected() throws Exception {
        assertThat(sound.get(), is(nullValue()));

        animal.set(Animal.CAT);
        assertThat(sound.get(), equalTo(Sound.MEOW));

        sound = null;
        animal = null;
        System.gc();
        Thread.sleep(5_000);
        assertThat(binding.wasGarbageCollected(), equalTo(true));
    }

    @Test
    public void animalToSound() {
        assertThat(animal.get(), is(nullValue()));
        assertThat(sound.get(), is(nullValue()));

        animal.set(Animal.DOG);
        assertThat(animal.get(), equalTo(Animal.DOG));
        assertThat(sound.get(), equalTo(Sound.BARK));

        animal.set(Animal.CAT);
        assertThat(animal.get(), equalTo(Animal.CAT));
        assertThat(sound.get(), equalTo(Sound.MEOW));

        animal.set(null);
        assertThat(animal.get(), is(nullValue()));
        assertThat(sound.get(), is(nullValue()));
    }

    @Test
    public void soundToAnimal() {
        assertThat(sound.get(), is(nullValue()));
        assertThat(animal.get(), is(nullValue()));

        sound.set(Sound.TWEET);
        assertThat(sound.get(), equalTo(Sound.TWEET));
        assertThat(animal.get(), equalTo(Animal.BIRD));

        sound.set(Sound.DUBIDIDU);
        assertThat(sound.get(), equalTo(Sound.DUBIDIDU));
        assertThat(animal.get(), equalTo(Animal.ELK));

        sound.set(null);
        assertThat(sound.get(), is(nullValue()));
        assertThat(animal.get(), is(nullValue()));
    }
}