<script setup lang="ts">

const props = defineProps<{
  showModal: boolean;
}>();

const emit = defineEmits(['update:showModal']);

/**
 * Close the modal dialog
 */
const closeModal = () => {
  emit('update:showModal', false);
};
</script>

<template>
  <div v-if="props.showModal">
    <slot name="header"></slot>
    <slot name="body"></slot>
    <slot name="footer">
      <button @click="closeModal">Close</button>
    </slot>
  </div>
</template>

<style lang="scss" scoped>

</style>
