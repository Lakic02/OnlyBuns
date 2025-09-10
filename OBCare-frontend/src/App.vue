<template>
  <div id="app">
    <h1>OBCare - Send Location Message</h1>
    <SendMessageForm />
  </div>
</template>

<script>
import SendMessageForm from './components/SendMessageForm.vue'

export default {
  components: {
    SendMessageForm
  }
}
</script>

<style>
body {
  font-family: Arial, sans-serif;
  padding: 20px;
}
</style>
